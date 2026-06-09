package i18n

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getDeviceLanguage(): String =
    NSLocale.currentLocale.languageCode.lowercase().take(2)
