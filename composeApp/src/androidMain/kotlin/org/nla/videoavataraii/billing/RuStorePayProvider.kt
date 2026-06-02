package org.nla.videoavataraii.billing

import android.app.Activity
import android.content.Intent
import ru.rustore.sdk.pay.RuStorePayClient
import ru.rustore.sdk.pay.model.purchase.request.PurchaseParams

/**
 * RuStore Pay SDK интеграция.
 * Документация: https://www.rustore.ru/help/sdk/pay/kotlin-java
 *
 * Инициализация вызывается из AndroidApp.onCreate()
 */
object RuStorePayProvider {

    fun init(activity: Activity) {
        // Pay SDK инициализируется автоматически через meta-data в AndroidManifest:
        // console_app_id_value и sdk_pay_scheme_value
        // Дополнительная инициализация не требуется для базового сценария
    }

    /**
     * Обработка deeplink при возврате из платёжного приложения.
     * Вызывать из MainActivity.onNewIntent()
     */
    fun handleDeepLink(intent: Intent) {
        RuStorePayClient.onNewIntent(intent)
    }

    /**
     * Покупка подписки (месячная / годовая).
     * productId берётся из Constants.Products
     */
    fun purchaseSubscription(
        activity: Activity,
        productId: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        RuStorePayClient.instance
            .purchaseProduct(
                params = PurchaseParams(productId = productId)
            )
            .addOnSuccessListener { result ->
                onSuccess()
            }
            .addOnFailureListener { throwable ->
                onFailure(throwable)
            }
    }

    /**
     * Покупка токенов (consumable product).
     * productId: tokens_1_80 / tokens_5_400 / tokens_10_800
     */
    fun purchaseTokens(
        activity: Activity,
        productId: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        RuStorePayClient.instance
            .purchaseProduct(
                params = PurchaseParams(productId = productId)
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { throwable ->
                onFailure(throwable)
            }
    }

    /**
     * Проверка доступности платежей (RuStore установлен + пользователь авторизован).
     */
    fun checkAvailability(
        onAvailable: () -> Unit,
        onUnavailable: (String) -> Unit,
    ) {
        RuStorePayClient.instance
            .checkPurchasesAvailability()
            .addOnSuccessListener { result ->
                if (result.isAvailable) {
                    onAvailable()
                } else {
                    onUnavailable(result.cause?.message ?: "RuStore недоступен")
                }
            }
            .addOnFailureListener { throwable ->
                onUnavailable(throwable.message ?: "Ошибка проверки")
            }
    }
}
