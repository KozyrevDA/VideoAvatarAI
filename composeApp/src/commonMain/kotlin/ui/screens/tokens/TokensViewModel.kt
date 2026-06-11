package ui.screens.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.Constants
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TokensUiState(
    val isLoading: Boolean = false,
    val selectedPack: Int = 2,
    val isPurchaseSuccess: Boolean = false,
    val purchasedCount: Int = 0,
    val error: String? = null,
    val tokensCount: Int = 0,
)

class TokensViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TokensUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.tokensCount.collect { _uiState.value = _uiState.value.copy(tokensCount = it) }
        }
    }

    fun selectPack(idx: Int) {
        _uiState.value = _uiState.value.copy(selectedPack = idx.coerceIn(0, 2))
    }

    fun purchase(onLaunchBilling: (productId: String, onResult: (Boolean, String?) -> Unit) -> Unit) {
        val (productId, count) = when (_uiState.value.selectedPack) {
            0    -> Constants.Products.TOKENS_1  to 1
            1    -> Constants.Products.TOKENS_5  to 5
            else -> Constants.Products.TOKENS_10 to 10
        }
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        onLaunchBilling(productId) { success, _ ->
            viewModelScope.launch {
                try {
                    if (success) {
                        repository.addTokens(count)
                        _uiState.value = _uiState.value.copy(isLoading = false, isPurchaseSuccess = true, purchasedCount = count)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
}
