package ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.VideoItem
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = false,
    val items: List<VideoItem> = emptyList(),
    val error: String? = null,
)

class HistoryViewModel(
    private val repository: AppRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getHistory()
                .onSuccess { items ->
                    _uiState.value = HistoryUiState(items = items)
                }
                .onFailure { e ->
                    _uiState.value = HistoryUiState(error = e.message)
                }
        }
    }
}
