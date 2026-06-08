package platform

import androidx.compose.runtime.Composable

// iOS: биллинг через App Store — реализуется через StoreKit
// В текущей версии возвращаем null (App Store покупки требуют отдельного Swift модуля)
@Composable
actual fun getBillingLauncher(): BillingLauncher? = null
