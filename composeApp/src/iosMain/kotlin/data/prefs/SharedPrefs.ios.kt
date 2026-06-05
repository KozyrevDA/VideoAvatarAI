package data.prefs

import platform.Foundation.NSUserDefaults

actual class SharedPrefs actual constructor() {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun isFirstOpen(): Boolean = !defaults.boolForKey(KEY_IS_FIRST_OPEN)
    actual fun setNotFirstOpen() = defaults.setBool(true, KEY_IS_FIRST_OPEN)
    actual fun isPastOnboarding(): Boolean = defaults.boolForKey(KEY_PAST_ONBOARDING)
    actual fun setPastOnboarding(value: Boolean) = defaults.setBool(value, KEY_PAST_ONBOARDING)
    actual fun getAuthToken(): String? = defaults.stringForKey(KEY_AUTH_TOKEN)
    actual fun setAuthToken(token: String) = defaults.setObject(token, KEY_AUTH_TOKEN)
    actual fun getUserId(): String? = defaults.stringForKey(KEY_USER_ID)
    actual fun setUserId(id: String) = defaults.setObject(id, KEY_USER_ID)
    actual fun getTokensCount(): Int = defaults.integerForKey(KEY_TOKENS_COUNT).toInt()
    actual fun setTokensCount(count: Int) = defaults.setInteger(count.toLong(), KEY_TOKENS_COUNT)
    actual fun isPro(): Boolean = defaults.boolForKey(KEY_IS_PRO)
    actual fun setPro(value: Boolean) = defaults.setBool(value, KEY_IS_PRO)
    actual fun getQuizRole(): String = defaults.stringForKey(KEY_QUIZ_ROLE) ?: ""
    actual fun setQuizRole(role: String) = defaults.setObject(role, KEY_QUIZ_ROLE)
    actual fun getQuizPains(): String = defaults.stringForKey(KEY_QUIZ_PAINS) ?: ""
    actual fun setQuizPains(pains: String) = defaults.setObject(pains, KEY_QUIZ_PAINS)
}
