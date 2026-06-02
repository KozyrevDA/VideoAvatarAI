package ui.screens.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TokensUiState(
    val isLoading: Boolean = false,
    val selectedPack: Int = 2, // 0=1шт, 1=5шт, 2=10шт
    val isPurchaseSuccess: Boolean = false,
    val error: String? = null,
    val tokensCount: Int = 0,
)

class TokensViewModel(
    private val repository: AppRepository,
) : ViewModel() {

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
        _uiState.value = _uiState.value.copy(selectedPack = idx)
    }

    fun purchase(activity: Any? = null) {
        val counts = listOf(1, 5, 10)
        val count = counts[_uiState.value.selectedPack]
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // TODO: RuStore / Google Play in-app purchase
            repository.addTokens(count)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isPurchaseSuccess = true,
            )
        }
    }
}
