package app

object Constants {
    const val BASE_URL = "https://api.videoavataraii.com"
    const val PACKAGE_NAME = "org.nla.videoavataraii"

    // Billing product IDs
    object Products {
        const val SUB_MONTHLY = "sub_monthly_499"
        const val SUB_YEARLY = "sub_yearly_2490"
        const val TOKENS_1 = "tokens_1_80"
        const val TOKENS_5 = "tokens_5_400"
        const val TOKENS_10 = "tokens_10_800"
    }

    // SharedPrefs keys
    object Prefs {
        const val FIRST_OPEN = "first_open"
        const val PAST_ONBOARDING = "past_onboarding"
        const val AUTH_TOKEN = "auth_token"
        const val TOKENS_COUNT = "tokens_count"
        const val IS_PRO = "is_pro"
        const val QUIZ_ROLE = "quiz_role"
        const val QUIZ_PAINS = "quiz_pains"
    }
}
