package org.nla.videoavataraii.billing

import android.app.Activity
import android.content.Intent

/**
 * RuStore Pay SDK — заглушка.
 * SDK подключается через RuStore Artifactory только локально.
 * В продакшн добавить: implementation(platform("ru.rustore.sdk:bom:X.X"))
 */
object RuStorePayProvider {
    fun init(activity: Activity) { /* no-op without SDK */ }
    fun handleDeepLink(intent: Intent) { /* no-op */ }
}
