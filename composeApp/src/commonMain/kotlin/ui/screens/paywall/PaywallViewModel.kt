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
    val selectedPlan: String = "yearly", // "monthly" | "yearly"
)

class PaywallViewModel(
    private val repository: AppRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState = _uiState.asStateFlow()

    val isPro = repository.isPro

    fun selectPlan(plan: String) {
        _uiState.value = _uiState.value.copy(selectedPlan = plan)
    }

    fun purchaseMonthly(activity: Any? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // TODO: RuStore / Google Play billing
            // Temporarily simulate success
            repository.setPro(true)
            repository.addTokens(5)
            repository.sharedPrefs.setNotFirstOpen()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isPurchaseSuccess = true,
            )
        }
    }

    fun purchaseYearly(activity: Any? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // TODO: RuStore / Google Play billing
            repository.setPro(true)
            repository.addTokens(5)
            repository.sharedPrefs.setNotFirstOpen()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isPurchaseSuccess = true,
            )
        }
    }
}
