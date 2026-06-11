package billing

import android.app.Activity
import android.content.Intent

// Alias — делегирует к org.nla.videoavataraii.billing.RuStorePayProvider
object RuStorePayProvider {
    fun init(activity: Activity) = org.nla.videoavataraii.billing.RuStorePayProvider.init(activity)
    fun handleDeepLink(intent: Intent) = org.nla.videoavataraii.billing.RuStorePayProvider.handleDeepLink(intent)
    fun isAvailable(activity: Activity) = org.nla.videoavataraii.billing.RuStorePayProvider.isAvailable(activity)
    fun pay(activity: Activity, productId: String, onResult: (Boolean, String?, String?) -> Unit) =
        org.nla.videoavataraii.billing.RuStorePayProvider.pay(activity, productId, onResult)
}
