package i18n

import androidx.compose.runtime.compositionLocalOf

/** Все строки приложения — меняется в зависимости от языка устройства. */
data class Strings(
    // ── Navigation ────────────────────────────────────────────────────────────
    val navHome: String,
    val navHistory: String,
    val navIdeas: String,
    val navSettings: String,

    // ── Common ────────────────────────────────────────────────────────────────
    val back: String,
    val create: String,
    val generate: String,
    val copy: String,
    val copyAll: String,
    val copied: String,
    val save: String,
    val share: String,
    val cancel: String,
    val loading: String,
    val error: String,
    val ready: String,
    val processing: String,
    val skip: String,
    val next: String,
    val done: String,

    // ── Home ──────────────────────────────────────────────────────────────────
    val homeGreeting: String,
    val homeRecent: String,
    val homeTokens: String,

    // ── Avatar ────────────────────────────────────────────────────────────────
    val avatarTitle: String,
    val avatarSubtitle: String,
    val avatarUploadPhoto: String,
    val avatarUploadHint: String,
    val avatarPhotoSelected: String,
    val avatarChangePhoto: String,
    val avatarTextLabel: String,
    val avatarTextPlaceholder: String,
    val avatarStyleLabel: String,
    val avatarStyleBusiness: String,
    val avatarStyleCasual: String,
    val avatarStyleExpert: String,
    val avatarCreateBtn: String,

    // ── Generating ────────────────────────────────────────────────────────────
    val genTitle: String,
    val genSubtitle: String,
    val genStep1: String,
    val genStep2: String,
    val genStep3: String,
    val genWaiting: String,
    val genTimeout: String,

    // ── Result ────────────────────────────────────────────────────────────────
    val resultTitle: String,
    val resultSave: String,
    val resultShare: String,
    val resultInstagram: String,
    val resultTikTok: String,

    // ── Text Post ─────────────────────────────────────────────────────────────
    val textTitle: String,
    val textTopicLabel: String,
    val textTopicPlaceholder: String,
    val textPlatformLabel: String,
    val textToneLabel: String,
    val textToneFriendly: String,
    val textToneExpert: String,
    val textToneFunny: String,
    val textToneMotivational: String,
    val textGenerateBtn: String,
    val textChangeBtn: String,
    val textAnotherBtn: String,

    // ── Ideas ─────────────────────────────────────────────────────────────────
    val ideasTitle: String,
    val ideasNicheLabel: String,
    val ideasNichePlaceholder: String,
    val ideasGenerateBtn: String,
    val ideasRefreshBtn: String,
    val ideasCopyAllBtn: String,
    val ideasCopied: String,
    val ideasAllCopied: String,
    val ideasEmpty: String,

    // ── Translate ─────────────────────────────────────────────────────────────
    val translateTitle: String,
    val translateSubtitle: String,
    val translateLangLabel: String,
    val translateVoiceLabel: String,
    val translateVoiceCloned: String,
    val translateVoiceStandard: String,
    val translateBtn: String,

    // ── History ───────────────────────────────────────────────────────────────
    val historyTitle: String,
    val historyEmpty: String,

    // ── Settings ──────────────────────────────────────────────────────────────
    val settingsTitle: String,
    val settingsVoice: String,
    val settingsVoiceDialog: String,
    val settingsNotifications: String,
    val settingsPrivacy: String,
    val settingsDeleteAccount: String,
    val settingsVideosCreated: String,

    // ── Paywall ───────────────────────────────────────────────────────────────
    val paywallTitle: String,
    val paywallSubtitle: String,
    val paywallMonthly: String,
    val paywallYearly: String,
    val paywallTrialBtn: String,
    val paywallDisclaimer: String,
    val paywallFeature1: String,
    val paywallFeature2: String,
    val paywallFeature3: String,
    val paywallFeature4: String,
    val paywallFeature5: String,

    // ── Tokens ────────────────────────────────────────────────────────────────
    val tokensTitle: String,
    val tokensBalance: String,
    val tokensBuyBtn: String,
    val tokensNeverExpire: String,

    // ── Onboarding ────────────────────────────────────────────────────────────
    val onboardingWho: String,
    val onboardingWhoHint: String,
    val onboardingRoleBlogger: String,
    val onboardingRoleBloggerSub: String,
    val onboardingRoleBusiness: String,
    val onboardingRoleBusinessSub: String,
    val onboardingRoleExpert: String,
    val onboardingRoleExpertSub: String,
    val onboardingRoleSmm: String,
    val onboardingRoleSmmSub: String,
    val onboardingPhoto: String,
    val onboardingPhotoHint: String,
    val onboardingPhotoBtn: String,
    val onboardingPhotoContinue: String,
    val onboardingPhotoSkip: String,
)

/** CompositionLocal — доступен во всех composable без явной передачи */
val LocalStrings = compositionLocalOf { englishStrings() }

/** Фабрика: язык устройства → нужные строки, fallback = English */
fun stringsForLanguage(lang: String): Strings = when (lang) {
    "ru"            -> russianStrings()
    "zh", "zh-hans" -> chineseStrings()
    "es"            -> spanishStrings()
    "ar"            -> arabicStrings()
    "pt"            -> portugueseStrings()
    "fr"            -> frenchStrings()
    "de"            -> germanStrings()
    "ja"            -> japaneseStrings()
    "ko"            -> koreanStrings()
    "tr"            -> turkishStrings()
    else            -> englishStrings()
}
