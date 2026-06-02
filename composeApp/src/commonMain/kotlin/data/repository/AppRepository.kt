package data.repository

import data.model.CaptionRequest
import data.model.GenerateAvatarRequest
import data.model.GenerateIdeasRequest
import data.model.GenerateTextPostRequest
import data.model.GenerationResult
import data.model.IdeasResult
import data.model.TextPostResult
import data.model.TranslateVideoRequest
import data.model.VideoItem
import data.network.ApiClient
import data.prefs.SharedPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppRepository(
    val sharedPrefs: SharedPrefs,
    private val apiClient: ApiClient,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _tokensCount = MutableStateFlow(sharedPrefs.getTokensCount())
    val tokensCount = _tokensCount.asStateFlow()

    private val _isPro = MutableStateFlow(sharedPrefs.isPro())
    val isPro = _isPro.asStateFlow()

    // ─── Avatar ──────────────────────────────────────────────────────────────

    suspend fun generateAvatar(request: GenerateAvatarRequest): Result<GenerationResult> =
        runCatching { apiClient.generateAvatar(request) }

    suspend fun checkGenerationStatus(id: String): Result<GenerationResult> =
        runCatching { apiClient.checkGenerationStatus(id) }

    // ─── Text Post ───────────────────────────────────────────────────────────

    suspend fun generateTextPost(request: GenerateTextPostRequest): Result<TextPostResult> =
        runCatching { apiClient.generateTextPost(request) }

    // ─── Ideas ───────────────────────────────────────────────────────────────

    suspend fun generateIdeas(request: GenerateIdeasRequest): Result<IdeasResult> =
        runCatching { apiClient.generateIdeas(request) }

    // ─── Translate ───────────────────────────────────────────────────────────

    suspend fun translateVideo(request: TranslateVideoRequest): Result<GenerationResult> =
        runCatching { apiClient.translateVideo(request) }

    // ─── History ─────────────────────────────────────────────────────────────

    suspend fun getHistory(): Result<List<VideoItem>> =
        runCatching { apiClient.getHistory() }

    // ─── Tokens ──────────────────────────────────────────────────────────────

    fun spendToken() {
        val current = _tokensCount.value
        if (current > 0) {
            val new = current - 1
            _tokensCount.value = new
            sharedPrefs.setTokensCount(new)
        }
    }

    fun addTokens(count: Int) {
        val new = _tokensCount.value + count
        _tokensCount.value = new
        sharedPrefs.setTokensCount(new)
    }

    fun setPro(value: Boolean) {
        _isPro.value = value
        sharedPrefs.setPro(value)
    }
}
