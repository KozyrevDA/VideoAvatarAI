package org.nla.videoavataraii.billing

import android.app.Activity
import android.content.Intent

/**
 * RuStore Pay — провайдер оплаты для российских пользователей.
 *
 * ⚠️  SDK не включён в CI-сборку (artifactory.rustore.ru недоступен из GitHub Actions).
 *
 * Чтобы включить реальный RuStore Pay:
 * 1. В composeApp/build.gradle.kts добавить:
 *    implementation(platform("ru.rustore.sdk:bom:2025.08.01"))
 *    implementation("ru.rustore.sdk:pay")
 * 2. Раскомментировать код ниже и убрать stub-реализацию
 */
object RuStorePayProvider {

    private var initialized = false

    /**
     * Инициализация при запуске Activity.
     * Вызывается из MainActivity.onCreate()
     */
    fun init(activity: Activity) {
        // TODO: раскомментировать после добавления SDK:
        // RuStorePayClient.init(
        //     application = activity.application,
        //     consoleApplicationId = activity.getString(R.string.CONSOLE_APPLICATION_ID),
        //     deeplinkScheme = "neuroset-video",
        // )
        initialized = true
    }

    /**
     * Обработка возврата из платёжного приложения (СБП, SberPay).
     * Вызывается из MainActivity.onNewIntent()
     */
    fun handleDeepLink(intent: Intent) {
        // TODO: RuStorePayClient.onNewIntent(intent)
    }

    /**
     * Проверяет доступность RuStore на устройстве.
     * RuStore доступен только на Android-устройствах с установленным RuStore.
     */
    fun isAvailable(activity: Activity): Boolean {
        return try {
            activity.packageManager.getPackageInfo("ru.vk.store", 0)
            true  // RuStore установлен
        } catch (_: Exception) {
            false  // RuStore не установлен — используем Google Play
        }
    }

    /**
     * Инициирует покупку через RuStore.
     *
     * @param productId  ID продукта из RuStore Console
     * @param onResult   callback: success, purchaseToken/null, errorMessage/null
     */
    fun pay(
        activity: Activity,
        productId: String,
        onResult: (success: Boolean, token: String?, error: String?) -> Unit,
    ) {
        // TODO: после добавления SDK:
        // RuStorePayClient.instance.purchase(productId)
        //     .addOnSuccessListener { result ->
        //         onResult(true, result.purchaseId, null)
        //     }
        //     .addOnFailureListener { throwable ->
        //         onResult(false, null, throwable.message)
        //     }

        // Временная заглушка пока SDK не добавлен
        onResult(false, null, "RuStore SDK не подключён. Добавь в build.gradle.kts:\nimplementation(platform(\"ru.rustore.sdk:bom:2025.08.01\"))\nimplementation(\"ru.rustore.sdk:pay\")")
    }
}
