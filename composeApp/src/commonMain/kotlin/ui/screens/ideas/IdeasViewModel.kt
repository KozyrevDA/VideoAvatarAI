package ui.screens.ideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.GenerateIdeasRequest
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IdeasUiState(
    val isLoading: Boolean = false,
    val niche: String = "",
    val platform: String = "instagram",
    val ideas: List<String> = emptyList(),
    val error: String? = null,
)

class IdeasViewModel(
    private val repository: AppRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdeasUiState())
    val uiState = _uiState.asStateFlow()

    fun onNicheChange(niche: String) {
        _uiState.value = _uiState.value.copy(niche = niche, error = null)
    }

    fun onPlatformSelect(platform: String) {
        _uiState.value = _uiState.value.copy(platform = platform)
    }

    fun generate() {
        val state = _uiState.value
        if (state.niche.isBlank()) {
            _uiState.value = state.copy(error = "Введи нишу")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            repository.generateIdeas(
                GenerateIdeasRequest(
                    niche = state.niche,
                    platform = state.platform,
                    count = 30,
                )
            ).onSuccess { result ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    ideas = result.ideas,
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка генерации",
                )
            }
        }
    }
}
