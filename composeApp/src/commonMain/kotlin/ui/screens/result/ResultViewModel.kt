package ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResultUiState(
    val isGenerating: Boolean = true,
    val progress1: Float = 1f,   // создаём аватар
    val progress2: Float = 0f,   // синхронизируем речь
    val progress3: Float = 0f,   // переводим
    val videoUrl: String? = null,
    val error: String? = null,
    val taskId: String = "",
)

class ResultViewModel(
    private val repository: AppRepository,
    private val videoId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState(taskId = videoId))
    val uiState = _uiState.asStateFlow()

    init {
        if (videoId.isNotBlank()) {
            startPolling()
        } else {
            simulateProgress()
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            var attempts = 0
            while (attempts < 60) {
                delay(3000)
                attempts++
                repository.checkGenerationStatus(videoId)
                    .onSuccess { result ->
                        when (result.status) {
                            "processing" -> {
                                val p = result.progress / 100f
                                _uiState.value = _uiState.value.copy(
                                    progress2 = (p * 1.5f).coerceAtMost(1f),
                                    progress3 = ((p - 0.66f) * 3f).coerceIn(0f, 1f),
                                )
                            }
                            "ready", "completed" -> {
                                _uiState.value = _uiState.value.copy(
                                    isGenerating = false,
                                    progress2 = 1f,
                                    progress3 = 1f,
                                    videoUrl = result.videoUrl,
                                )
                                return@launch
                            }
                            "error" -> {
                                _uiState.value = _uiState.value.copy(
                                    isGenerating = false,
                                    error = result.message,
                                )
                                return@launch
                            }
                        }
                    }
            }
            // Timeout
            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                error = "Превышено время ожидания",
            )
        }
    }

    // Demo simulation when no real taskId
    private fun simulateProgress() {
        viewModelScope.launch {
            delay(800)
            _uiState.value = _uiState.value.copy(progress2 = 0.4f)
            delay(1000)
            _uiState.value = _uiState.value.copy(progress2 = 0.7f)
            delay(800)
            _uiState.value = _uiState.value.copy(progress2 = 1f, progress3 = 0.3f)
            delay(1000)
            _uiState.value = _uiState.value.copy(progress3 = 0.7f)
            delay(600)
            _uiState.value = _uiState.value.copy(
                progress3 = 1f,
                isGenerating = false,
                videoUrl = "demo",
            )
        }
    }
}
