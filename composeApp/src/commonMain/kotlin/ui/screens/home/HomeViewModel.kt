package ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.VideoItem
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val recentItems: List<VideoItem> = emptyList(),
    val tokensCount: Int = 0,
    val isPro: Boolean = false,
    val userName: String = "",
)

class HomeViewModel(
    private val repository: AppRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.tokensCount.collect { tokens ->
                _uiState.value = _uiState.value.copy(tokensCount = tokens)
            }
        }
        viewModelScope.launch {
            repository.isPro.collect { pro ->
                _uiState.value = _uiState.value.copy(isPro = pro)
            }
        }
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getHistory()
                .onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        recentItems = items.take(5),
                        isLoading = false,
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }
}
