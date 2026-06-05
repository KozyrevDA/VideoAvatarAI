package data.prefs

const val KEY_IS_FIRST_OPEN   = "is_first_open"
const val KEY_PAST_ONBOARDING = "past_onboarding"
const val KEY_AUTH_TOKEN      = "auth_token"
const val KEY_USER_ID         = "user_id"
const val KEY_TOKENS_COUNT    = "tokens_count"
const val KEY_IS_PRO          = "is_pro"
const val KEY_QUIZ_ROLE       = "quiz_role"
const val KEY_QUIZ_PAINS      = "quiz_pains"

expect class SharedPrefs() {
    fun isFirstOpen(): Boolean
    fun setNotFirstOpen()
    fun isPastOnboarding(): Boolean
    fun setPastOnboarding(value: Boolean)
    fun getAuthToken(): String?
    fun setAuthToken(token: String)
    fun getUserId(): String?
    fun setUserId(id: String)
    fun getTokensCount(): Int
    fun setTokensCount(count: Int)
    fun isPro(): Boolean
    fun setPro(value: Boolean)
    fun getQuizRole(): String
    fun setQuizRole(role: String)
    fun getQuizPains(): String
    fun setQuizPains(pains: String)
}
