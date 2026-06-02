package ui.navigation

import androidx.navigation.NavHostController

class AppNavigationActions(private val navController: NavHostController) {

    fun navigateToOnboarding(popUpTo: Boolean = false) {
        navController.navigate(AppNavDestinations.OnboardingDestination) {
            if (popUpTo) popUpTo(0) { inclusive = true }
        }
    }

    fun navigateToPaywall(prevScreen: String) {
        navController.navigate(AppNavDestinations.PaywallDestination(prevScreen))
    }

    fun navigateToHome(popUpTo: Boolean = false) {
        navController.navigate(AppNavDestinations.HomeDestination) {
            if (popUpTo) popUpTo(0) { inclusive = true }
        }
    }

    fun navigateToAvatar() {
        navController.navigate(AppNavDestinations.AvatarDestination)
    }

    fun navigateToResult(videoId: String) {
        navController.navigate(AppNavDestinations.ResultDestination(videoId))
    }

    fun navigateToTextPost() {
        navController.navigate(AppNavDestinations.TextPostDestination)
    }

    fun navigateToIdeas() {
        navController.navigate(AppNavDestinations.IdeasDestination)
    }

    fun navigateToTranslate(videoId: String) {
        navController.navigate(AppNavDestinations.TranslateDestination(videoId))
    }

    fun navigateToHistory() {
        navController.navigate(AppNavDestinations.HistoryDestination)
    }

    fun navigateToSettings() {
        navController.navigate(AppNavDestinations.SettingsDestination)
    }

    fun navigateToTokens() {
        navController.navigate(AppNavDestinations.TokensDestination)
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}
