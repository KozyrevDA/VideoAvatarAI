package di

import data.network.ApiClient
import data.prefs.SharedPrefs
import data.repository.AppRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import ui.screens.avatar.AvatarViewModel
import ui.screens.history.HistoryViewModel
import ui.screens.home.HomeViewModel
import ui.screens.ideas.IdeasViewModel
import ui.screens.onboarding.OnboardingViewModel
import ui.screens.paywall.PaywallViewModel
import ui.screens.result.ResultViewModel
import ui.screens.settings.SettingsViewModel
import ui.screens.textpost.TextPostViewModel
import ui.screens.tokens.TokensViewModel
import ui.screens.translate.TranslateViewModel

object Koin {
    fun koinConfiguration() = koinApplication {
        modules(appModule())
    }.koin

    private fun appModule() = module {
        single { SharedPrefs() }
        single {
            ApiClient(
                baseUrl = "https://api.videoavataraii.com",
                token = get<SharedPrefs>().getAuthToken() ?: "",
            )
        }
        single { AppRepository(sharedPrefs = get(), apiClient = get()) }

        viewModel { OnboardingViewModel(get()) }
        viewModel { PaywallViewModel(get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { AvatarViewModel(get()) }
        viewModel { (videoId: String) -> ResultViewModel(get(), videoId) }
        viewModel { TextPostViewModel(get()) }
        viewModel { IdeasViewModel(get()) }
        viewModel { (videoId: String) -> TranslateViewModel(get(), videoId) }
        viewModel { HistoryViewModel(get()) }
        viewModel { SettingsViewModel(get()) }
        viewModel { TokensViewModel(get()) }
    }
}
