package platform

interface BillingLauncher {
    fun launch(productId: String, onResult: (success: Boolean, purchaseToken: String?) -> Unit)
}

@androidx.compose.runtime.Composable
expect fun getBillingLauncher(): BillingLauncher?
