package i18n

import java.util.Locale

actual fun getDeviceLanguage(): String =
    Locale.getDefault().language.lowercase().take(2)
