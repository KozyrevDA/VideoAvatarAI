package billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.ProductType
import kotlinx.coroutines.suspendCancellableCoroutine
import org.nla.videoavataraii.app.AndroidApp
import kotlin.coroutines.resume

sealed class BillingResult {
    data class Success(val purchaseToken: String, val productId: String) : BillingResult()
    data class Error(val message: String) : BillingResult()
    object Cancelled : BillingResult()
}

interface BillingProvider {
    suspend fun purchase(activity: Activity, productId: String): BillingResult
    suspend fun isAvailable(): Boolean
}

private typealias GPBillingResult = com.android.billingclient.api.BillingResult

class GooglePlayBillingProvider : BillingProvider {

    private val context: Context get() = AndroidApp.instance

    override suspend fun isAvailable(): Boolean = try { connectClient().isReady } catch (_: Exception) { false }

    override suspend fun purchase(activity: Activity, productId: String): BillingResult {
        return try {
            val productType = if (productId.startsWith("sub_")) ProductType.SUBS else ProductType.INAPP
            val details = queryDetails(productId, productType) ?: return BillingResult.Error("Product not found")
            val flowParams = buildFlow(details, productType) ?: return BillingResult.Error("Build flow error")

            suspendCancellableCoroutine { cont ->
                val client = BillingClient.newBuilder(context)
                    .setListener { r, purchases ->
                        when (r.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
                                val p = purchases?.find { it.products.contains(productId) }
                                cont.resume(if (p != null) BillingResult.Success(p.purchaseToken, productId) else BillingResult.Cancelled)
                            }
                            BillingClient.BillingResponseCode.USER_CANCELED -> cont.resume(BillingResult.Cancelled)
                            else -> cont.resume(BillingResult.Error("GP ${r.responseCode}"))
                        }
                    }
                    .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                    .build()
                client.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(r: GPBillingResult) { client.launchBillingFlow(activity, flowParams) }
                    override fun onBillingServiceDisconnected() { cont.resume(BillingResult.Error("GP disconnected")) }
                })
            }
        } catch (e: Exception) { BillingResult.Error(e.message ?: "Billing error") }
    }

    private suspend fun connectClient(): BillingClient = suspendCancellableCoroutine { cont ->
        val c = BillingClient.newBuilder(context).setListener { _, _ -> }
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()).build()
        c.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(r: GPBillingResult) = cont.resume(c)
            override fun onBillingServiceDisconnected() = cont.resume(c)
        })
    }

    private suspend fun queryDetails(id: String, type: String): ProductDetails? {
        val c = connectClient()
        return suspendCancellableCoroutine { cont ->
            c.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder()
                .setProductList(listOf(QueryProductDetailsParams.Product.newBuilder().setProductId(id).setProductType(type).build()))
                .build()) { _, list -> cont.resume(list.firstOrNull()) }
        }
    }

    private fun buildFlow(d: ProductDetails, type: String): BillingFlowParams? {
        return if (type == ProductType.SUBS) {
            val token = d.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return null
            BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(d).setOfferToken(token).build()
            )).build()
        } else {
            BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(d).build()
            )).build()
        }
    }
}
