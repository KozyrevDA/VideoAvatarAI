package ui.screens.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TokensUiState(
    val isLoading: Boolean = false,
    val selectedPack: Int = 2,
    val isPurchaseSuccess: Boolean = false,
    val error: String? = null,
    val tokensCount: Int = 0,
)

class TokensViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TokensUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.tokensCount.collect { tokens ->
                _uiState.value = _uiState.value.copy(tokensCount = tokens)
            }
        }
    }

    fun selectPack(idx: Int) {
        _uiState.value = _uiState.value.copy(selectedPack = idx.coerceIn(0, 2))
    }

    fun purchase() {
        val counts = listOf(1, 5, 10)
        val count = counts[_uiState.value.selectedPack]
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                // TODO: RuStore Pay / Google Play in-app purchase
                repository.addTokens(count)
                _uiState.value = _uiState.value.copy(isLoading = false, isPurchaseSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка покупки",
                )
            }
        }
    }
}
