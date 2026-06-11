package ui.screens.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.Constants
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaywallUiState(
    val isLoading: Boolean = false,
    val isPurchaseSuccess: Boolean = false,
    val selectedPlan: String = "yearly",
    val error: String? = null,
)

class PaywallViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState = _uiState.asStateFlow()

    fun selectPlan(plan: String) {
        _uiState.value = _uiState.value.copy(selectedPlan = plan)
    }

    fun purchase(onLaunchBilling: (productId: String, onResult: (Boolean, String?) -> Unit) -> Unit) {
        val productId = if (_uiState.value.selectedPlan == "yearly") Constants.Products.SUB_YEARLY
                        else Constants.Products.SUB_MONTHLY
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        onLaunchBilling(productId) { success, errorOrToken ->
            viewModelScope.launch {
                try {
                    if (success) {
                        repository.setPro(true)
                        repository.addTokens(5)
                        _uiState.value = _uiState.value.copy(isLoading = false, isPurchaseSuccess = true)
                    } else {
                        // Показываем ошибку если она есть (не просто отмена)
                        val errorMsg = if (errorOrToken != null && errorOrToken != "null") errorOrToken else null
                        _uiState.value = _uiState.value.copy(isLoading = false, error = errorMsg)
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
}
