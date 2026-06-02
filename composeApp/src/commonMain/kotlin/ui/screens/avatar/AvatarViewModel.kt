package ui.screens.avatar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.AvatarStyle
import data.model.GenerateAvatarRequest
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AvatarUiState(
    val isLoading: Boolean = false,
    val text: String = "",
    val selectedStyle: AvatarStyle = AvatarStyle.BUSINESS,
    val selectedPlatform: String = "instagram",
    val photoBase64: String? = null,
    val tokensCount: Int = 0,
    val error: String? = null,
    val generatedTaskId: String? = null,
)

class AvatarViewModel(
    private val repository: AppRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvatarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.tokensCount.collect { tokens ->
                _uiState.value = _uiState.value.copy(tokensCount = tokens)
            }
        }
    }

    fun onTextChange(text: String) {
        _uiState.value = _uiState.value.copy(text = text, error = null)
    }

    fun onStyleSelect(style: AvatarStyle) {
        _uiState.value = _uiState.value.copy(selectedStyle = style)
    }

    fun onPlatformSelect(platform: String) {
        _uiState.value = _uiState.value.copy(selectedPlatform = platform)
    }

    fun onPhotoSelected(base64: String) {
        _uiState.value = _uiState.value.copy(photoBase64 = base64)
    }

    fun generate(onSuccess: (taskId: String) -> Unit) {
        val state = _uiState.value
        if (state.text.isBlank()) {
            _uiState.value = state.copy(error = "Введи текст для аватара")
            return
        }
        if (state.tokensCount <= 0) {
            _uiState.value = state.copy(error = "Токены закончились")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            repository.generateAvatar(
                GenerateAvatarRequest(
                    photoBase64 = state.photoBase64 ?: "",
                    text = state.text,
                    style = state.selectedStyle,
                    platform = state.selectedPlatform,
                    language = "ru",
                )
            ).onSuccess { result ->
                repository.spendToken()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generatedTaskId = result.id,
                )
                onSuccess(result.id)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка генерации",
                )
            }
        }
    }
}
