package data.prefs

import android.content.Context
import android.content.SharedPreferences

actual class SharedPrefs(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("video_avatar_ai_prefs", Context.MODE_PRIVATE)
    }

    actual fun isFirstOpen(): Boolean = prefs.getBoolean(KEY_IS_FIRST_OPEN, true)
    actual fun setNotFirstOpen() = prefs.edit().putBoolean(KEY_IS_FIRST_OPEN, false).apply()
    actual fun isPastOnboarding(): Boolean = prefs.getBoolean(KEY_PAST_ONBOARDING, false)
    actual fun setPastOnboarding(value: Boolean) = prefs.edit().putBoolean(KEY_PAST_ONBOARDING, value).apply()
    actual fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)
    actual fun setAuthToken(token: String) = prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    actual fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    actual fun setUserId(id: String) = prefs.edit().putString(KEY_USER_ID, id).apply()
    actual fun getTokensCount(): Int = prefs.getInt(KEY_TOKENS_COUNT, 0)
    actual fun setTokensCount(count: Int) = prefs.edit().putInt(KEY_TOKENS_COUNT, count).apply()
    actual fun isPro(): Boolean = prefs.getBoolean(KEY_IS_PRO, false)
    actual fun setPro(value: Boolean) = prefs.edit().putBoolean(KEY_IS_PRO, value).apply()
    actual fun getQuizRole(): String = prefs.getString(KEY_QUIZ_ROLE, "") ?: ""
    actual fun setQuizRole(role: String) = prefs.edit().putString(KEY_QUIZ_ROLE, role).apply()
    actual fun getQuizPains(): String = prefs.getString(KEY_QUIZ_PAINS, "") ?: ""
    actual fun setQuizPains(pains: String) = prefs.edit().putString(KEY_QUIZ_PAINS, pains).apply()
}
