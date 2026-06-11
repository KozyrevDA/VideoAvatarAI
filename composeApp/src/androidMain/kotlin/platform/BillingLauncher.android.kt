package platform

import android.app.Activity
import android.content.pm.ApplicationInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import billing.BillingResult
import billing.GooglePlayBillingProvider
import billing.RuStorePayProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
actual fun getBillingLauncher(): BillingLauncher? {
    val activity = LocalContext.current as? Activity ?: return null
    return remember(activity) {
        object : BillingLauncher {
            override fun launch(productId: String, onResult: (Boolean, String?) -> Unit) {
                CoroutineScope(Dispatchers.Main).launch {

                    // DEBUG: симулируем покупку для тестирования UI
                    if (isDebugBuild(activity)) {
                        delay(1500)
                        onResult(true, "debug-token-${System.currentTimeMillis()}")
                        return@launch
                    }

                    // PRODUCTION: выбираем провайдера по языку устройства
                    val isRussian = Locale.getDefault().language == "ru"

                    if (isRussian && RuStorePayProvider.isAvailable(activity)) {
                        // RuStore Pay — для российских пользователей
                        RuStorePayProvider.pay(activity, productId) { success, token, error ->
                            onResult(success, if (success) token else error)
                        }
                    } else {
                        // Google Play Billing — для всех остальных
                        when (val result = GooglePlayBillingProvider(activity).purchase(activity, productId)) {
                            is BillingResult.Success   -> onResult(true, result.purchaseToken)
                            is BillingResult.Cancelled -> onResult(false, null)
                            is BillingResult.Error     -> onResult(false, result.message)
                        }
                    }
                }
            }

            private fun isDebugBuild(activity: Activity): Boolean = try {
                val appInfo = activity.packageManager.getApplicationInfo(activity.packageName, 0)
                (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            } catch (_: Exception) { false }
        }
    }
}
