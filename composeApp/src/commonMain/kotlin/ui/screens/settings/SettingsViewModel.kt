package ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isPro: Boolean = false,
    val tokensCount: Int = 0,
    val voiceCloned: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val error: String? = null,
)

class SettingsViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.isPro.collect { pro ->
                _uiState.value = _uiState.value.copy(isPro = pro)
            }
        }
        viewModelScope.launch {
            repository.tokensCount.collect { tokens ->
                _uiState.value = _uiState.value.copy(tokensCount = tokens)
            }
        }
    }

    fun toggleNotifications() {
        _uiState.value = _uiState.value.copy(
            notificationsEnabled = !_uiState.value.notificationsEnabled,
        )
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            try {
                // TODO: отмена через Billing SDK
                repository.setPro(false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
