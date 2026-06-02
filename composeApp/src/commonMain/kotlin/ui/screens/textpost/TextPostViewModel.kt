package ui.screens.textpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.GenerateTextPostRequest
import data.model.TextPostResult
import data.model.TextTone
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TextPostUiState(
    val isLoading: Boolean = false,
    val topic: String = "",
    val platform: String = "instagram",
    val tone: TextTone = TextTone.FRIENDLY,
    val result: TextPostResult? = null,
    val error: String? = null,
)

class TextPostViewModel(
    private val repository: AppRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextPostUiState())
    val uiState = _uiState.asStateFlow()

    fun onTopicChange(topic: String) {
        _uiState.value = _uiState.value.copy(topic = topic, error = null)
    }

    fun onPlatformSelect(platform: String) {
        _uiState.value = _uiState.value.copy(platform = platform)
    }

    fun onToneSelect(tone: TextTone) {
        _uiState.value = _uiState.value.copy(tone = tone)
    }

    fun generate() {
        val state = _uiState.value
        if (state.topic.isBlank()) {
            _uiState.value = state.copy(error = "Введи тему поста")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            repository.generateTextPost(
                GenerateTextPostRequest(
                    topic = state.topic,
                    platform = state.platform,
                    tone = state.tone,
                )
            ).onSuccess { result ->
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка генерации",
                )
            }
        }
    }
}
