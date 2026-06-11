package org.nla.videoavataraii.billing

import android.app.Activity
import android.content.Intent
import android.util.Log

/**
 * RuStore Pay — оплата для российских пользователей.
 *
 * Подключение SDK (один из вариантов):
 *
 * Вариант A — локальные AAR (работает в CI):
 *   1. Скачай: https://www.rustore.ru/help/sdk/pay/kotlin-java/latest/
 *   2. Положи в папку libs/ репозитория:
 *      libs/rustore-pay.aar
 *   3. Закоммить: git add libs/ && git commit -m "Add RuStore SDK"
 *
 * Вариант B — через Artifactory (только локальная сборка):
 *   implementation(platform("ru.rustore.sdk:bom:2025.08.01"))
 *   implementation("ru.rustore.sdk:pay")
 */
object RuStorePayProvider {

    private const val TAG = "RuStorePayProvider"
    private var isInitialized = false

    // Проверяем наличие SDK через reflection (не требует compile-time зависимости)
    private val isSdkAvailable: Boolean by lazy {
        try {
            Class.forName("ru.rustore.sdk.billingclient.RuStoreBillingClient")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    fun init(activity: Activity) {
        if (!isSdkAvailable) {
            Log.w(TAG, "RuStore SDK не найден в APK. Добавь AAR в libs/")
            return
        }
        try {
            // Инициализация через reflection
            val clientClass = Class.forName("ru.rustore.sdk.billingclient.RuStoreBillingClient")
            val method = clientClass.getMethod("init",
                android.app.Application::class.java,
                String::class.java,
                String::class.java
            )
            val appId = try {
                activity.getString(activity.resources.getIdentifier(
                    "CONSOLE_APPLICATION_ID", "string", activity.packageName))
            } catch (_: Exception) { "2351028944" }

            method.invoke(null, activity.application, appId, "neuroset-video")
            isInitialized = true
            Log.i(TAG, "RuStore SDK инициализирован")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка инициализации RuStore: ${e.message}")
        }
    }

    fun handleDeepLink(intent: Intent) {
        if (!isSdkAvailable || !isInitialized) return
        try {
            val clientClass = Class.forName("ru.rustore.sdk.billingclient.RuStoreBillingClient")
            val instanceMethod = clientClass.getMethod("getInstance")
            val instance = instanceMethod.invoke(null)
            instance?.javaClass?.getMethod("onNewIntent", Intent::class.java)
                ?.invoke(instance, intent)
        } catch (_: Exception) {}
    }

    fun isAvailable(activity: Activity): Boolean {
        // 1. SDK должен быть в APK
        if (!isSdkAvailable) return false
        // 2. RuStore должен быть установлен на устройстве
        return try {
            activity.packageManager.getPackageInfo("ru.vk.store", 0)
            true
        } catch (_: Exception) { false }
    }

    fun pay(
        activity: Activity,
        productId: String,
        onResult: (success: Boolean, token: String?, error: String?) -> Unit,
    ) {
        if (!isSdkAvailable) {
            onResult(false, null, "RuStore SDK не найден. Добавь libs/rustore-pay.aar")
            return
        }
        if (!isInitialized) {
            init(activity)
        }
        try {
            // Запуск покупки через reflection
            val clientClass = Class.forName("ru.rustore.sdk.billingclient.RuStoreBillingClient")
            val instance = clientClass.getMethod("getInstance").invoke(null)
            val purchaseMethod = instance!!.javaClass.getMethod(
                "purchaseProduct", String::class.java, Int::class.java,
                String::class.java, String::class.java
            )
            val taskClass = Class.forName("ru.rustore.sdk.core.tasks.Task")
            val task = purchaseMethod.invoke(instance, productId, 1, null, null)

            task!!.javaClass
                .getMethod("addOnSuccessListener",
                    Class.forName("ru.rustore.sdk.core.tasks.OnSuccessListener"))
                .invoke(task, java.lang.reflect.Proxy.newProxyInstance(
                    task.javaClass.classLoader,
                    arrayOf(Class.forName("ru.rustore.sdk.core.tasks.OnSuccessListener"))
                ) { _, _, args ->
                    val result = args[0]
                    val purchaseId = result?.javaClass
                        ?.getMethod("getPurchaseId")?.invoke(result)?.toString() ?: ""
                    onResult(true, purchaseId, null)
                    null
                })

            task.javaClass
                .getMethod("addOnFailureListener",
                    Class.forName("ru.rustore.sdk.core.tasks.OnFailureListener"))
                .invoke(task, java.lang.reflect.Proxy.newProxyInstance(
                    task.javaClass.classLoader,
                    arrayOf(Class.forName("ru.rustore.sdk.core.tasks.OnFailureListener"))
                ) { _, _, args ->
                    onResult(false, null, (args[0] as? Throwable)?.message ?: "Ошибка RuStore")
                    null
                })

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка RuStore purchase: ${e.message}")
            onResult(false, null, "Ошибка RuStore: ${e.message}")
        }
    }
}
