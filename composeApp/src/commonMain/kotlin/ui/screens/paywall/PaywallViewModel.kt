package ui.screens.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaywallUiState(
    val isLoading: Boolean = false,
    val isPurchaseSuccess: Boolean = false,
    val error: String? = null,
    val selectedPlan: String = "yearly",
)

class PaywallViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState = _uiState.asStateFlow()

    fun selectPlan(plan: String) {
        _uiState.value = _uiState.value.copy(selectedPlan = plan)
    }

    fun purchaseMonthly() = purchase("monthly")
    fun purchaseYearly()  = purchase("yearly")

    private fun purchase(type: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                // TODO: подключить RuStore Pay / Google Play Billing
                repository.setPro(true)
                repository.addTokens(5)
                repository.sharedPrefs.setNotFirstOpen()
                _uiState.value = _uiState.value.copy(isLoading = false, isPurchaseSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка оплаты",
                )
            }
        }
    }
}
