package ui.screens.translate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.TranslateVideoRequest
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TranslateUiState(
    val isLoading: Boolean = false,
    val selectedLanguage: String = "English",
    val useClonedVoice: Boolean = true,
    val tokensCount: Int = 0,
    val error: String? = null,
    val resultTaskId: String? = null,
)

class TranslateViewModel(
    private val repository: AppRepository,
    private val videoId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslateUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.tokensCount.collect { tokens ->
                _uiState.value = _uiState.value.copy(tokensCount = tokens)
            }
        }
    }

    fun onLanguageSelect(lang: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = lang)
    }

    fun onVoiceToggle(useCloned: Boolean) {
        _uiState.value = _uiState.value.copy(useClonedVoice = useCloned)
    }

    fun translate(onSuccess: (taskId: String) -> Unit) {
        val state = _uiState.value
        if (state.tokensCount <= 0) {
            _uiState.value = state.copy(error = "Токены закончились")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            repository.translateVideo(
                TranslateVideoRequest(
                    videoId = videoId,
                    targetLanguage = state.selectedLanguage,
                    useClonedVoice = state.useClonedVoice,
                )
            ).onSuccess { result ->
                repository.spendToken()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    resultTaskId = result.id,
                )
                onSuccess(result.id)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка перевода",
                )
            }
        }
    }
}
