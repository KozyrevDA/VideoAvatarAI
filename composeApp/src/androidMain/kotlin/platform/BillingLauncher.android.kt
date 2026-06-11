package platform

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import billing.BillingResult
import billing.GooglePlayBillingProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
actual fun getBillingLauncher(): BillingLauncher? {
    val activity = LocalContext.current as? Activity ?: return null
    return remember(activity) {
        object : BillingLauncher {
            override fun launch(productId: String, onResult: (Boolean, String?) -> Unit) {
                CoroutineScope(Dispatchers.Main).launch {
                    // DEBUG BUILD: симулируем успешную покупку для тестирования UI
                    // В release-сборке заменить на реальный биллинг
                    if (android.os.Build.TYPE == "eng" || isDebugBuild(activity)) {
                        // Симуляция задержки как при реальной покупке
                        kotlinx.coroutines.delay(1500)
                        onResult(true, "debug-purchase-token-${System.currentTimeMillis()}")
                        return@launch
                    }

                    when (val result = GooglePlayBillingProvider(activity).purchase(activity, productId)) {
                        is BillingResult.Success   -> onResult(true, result.purchaseToken)
                        is BillingResult.Cancelled -> onResult(false, null)
                        is BillingResult.Error     -> {
                            android.util.Log.e("Billing", "Error: \${result.message}")
                            onResult(false, result.message)
                        }
                    }
                }
            }

            private fun isDebugBuild(activity: Activity): Boolean {
                return try {
                    val flags = activity.packageManager
                        .getPackageInfo(activity.packageName, 0).applicationInfo.flags
                    (flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
                } catch (_: Exception) { false }
            }
        }
    }
}
