package data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val tokensCount: Int = 0,
    val isPro: Boolean = false,
    val subscriptionType: String = "",
    val nextPaymentDate: String = "",
    val voiceCloned: Boolean = false,
)

@Serializable
data class VideoItem(
    val id: String = "",
    val type: String = "avatar",
    val title: String = "",
    val thumbnailUrl: String = "",
    val videoUrl: String = "",
    val language: String = "ru",
    val platform: String = "instagram",
    val status: String = "processing",
    val createdAt: String = "",
)

@Serializable
data class GenerateAvatarRequest(
    val photoBase64: String,
    val text: String,
    val style: AvatarStyle = AvatarStyle.BUSINESS,
    val platform: String = "instagram",
    val language: String = "ru",
    val voiceId: String? = null,  // Fish Audio reference_id для клонированного голоса
)

enum class AvatarStyle { BUSINESS, CASUAL, EXPERT }

@Serializable
data class GenerateTextPostRequest(
    val topic: String,
    val platform: String = "instagram",
    val tone: TextTone = TextTone.FRIENDLY,
)

enum class TextTone { FRIENDLY, EXPERT, FUNNY, MOTIVATIONAL }

@Serializable
data class GenerateIdeasRequest(
    val niche: String,
    val platform: String = "instagram",
    val count: Int = 30,
)

@Serializable
data class TranslateVideoRequest(
    val videoId: String,
    val targetLanguage: String,
    val useClonedVoice: Boolean = true,
    val sourceText: String = "",
)

@Serializable
data class GenerationResult(
    val id: String = "",
    val taskId: String = "",
    val status: String = "processing",
    val videoUrl: String? = null,
    val progress: Int = 0,
    val message: String = "",
    val errorMessage: String? = null,
) {
    val effectiveId: String get() = id.ifBlank { taskId }
}

@Serializable
data class TextPostResult(
    val text: String = "",
    val hashtags: List<String> = emptyList(),
    val platform: String = "",
    val characterCount: Int = 0,
)

@Serializable
data class IdeasResult(val ideas: List<String> = emptyList())

@Serializable
data class TokenPack(
    val id: String,
    val count: Int,
    val price: Int,
    val priceLabel: String,
    val isPopular: Boolean = false,
)

@Serializable
data class SubscriptionPlan(
    val id: String,
    val type: String,
    val price: Int,
    val priceLabel: String,
    val tokensPerMonth: Int,
    val discount: String = "",
)

val tokenPacks = listOf(
    TokenPack("tokens_1", 1, 80, "80 ₽"),
    TokenPack("tokens_5", 5, 400, "400 ₽"),
    TokenPack("tokens_10", 10, 800, "800 ₽", isPopular = true),
)

val subscriptionPlans = listOf(
    SubscriptionPlan("sub_monthly", "monthly", 499, "499 ₽/мес", 5),
    SubscriptionPlan("sub_yearly", "yearly", 1, "1 ₽ → 2 490 ₽/год", 5, "-58%"),
)

val languages = listOf(
    "English", "Español", "中文", "Deutsch", "Français",
    "日本語", "Português", "العربية", "한국어", "Hindi",
    "Turkish", "Italian", "Polish", "Dutch", "Swedish",
    "Русский", "Українська", "Қазақша",
)

val platforms = listOf("Instagram", "TikTok", "VK", "YouTube")
