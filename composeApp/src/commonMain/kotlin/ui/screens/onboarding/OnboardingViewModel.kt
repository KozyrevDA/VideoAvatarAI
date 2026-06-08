package ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: AppRepository,
) : ViewModel() {

    private val _isPastOnboarding = MutableStateFlow(false)
    val isPastOnboarding = _isPastOnboarding.asStateFlow()

    fun saveQuizRole(role: String) {
        repository.sharedPrefs.setQuizRole(role)
    }

    fun saveQuizPains(pains: List<String>) {
        repository.sharedPrefs.setQuizPains(pains.joinToString(","))
    }

    fun setPastOnboarding() {
        viewModelScope.launch {
            try {
                repository.sharedPrefs.setPastOnboarding(true)
                _isPastOnboarding.value = true
            } catch (e: Exception) {
                // SharedPrefs запись не должна фейлить, но защищаемся
                _isPastOnboarding.value = true
            }
        }
    }

    fun isFirstOpen(): Boolean = repository.sharedPrefs.isFirstOpen()
}
