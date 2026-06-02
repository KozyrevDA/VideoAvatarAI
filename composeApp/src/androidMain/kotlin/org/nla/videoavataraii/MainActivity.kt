package org.nla.videoavataraii

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.App
import org.nla.videoavataraii.billing.RuStorePayProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RuStorePayProvider.init(this)
        setContent { App() }
    }

    // Обязательно для Pay SDK — возврат из платёжного приложения (СБП, SberPay)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        RuStorePayProvider.handleDeepLink(intent)
    }
}
