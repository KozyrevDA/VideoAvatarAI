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
                    when (val result = GooglePlayBillingProvider().purchase(activity, productId)) {
                        is BillingResult.Success   -> onResult(true, result.purchaseToken)
                        is BillingResult.Cancelled -> onResult(false, null)
                        is BillingResult.Error     -> onResult(false, null)
                    }
                }
            }
        }
    }
}
