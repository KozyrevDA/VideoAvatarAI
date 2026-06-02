package ui.navigation

import kotlinx.serialization.Serializable

sealed class AppNavDestinations {

    @Serializable
    object LaunchDestination : AppNavDestinations()

    @Serializable
    object OnboardingDestination : AppNavDestinations()

    @Serializable
    data class PaywallDestination(val prevScreen: String) : AppNavDestinations()

    @Serializable
    object HomeDestination : AppNavDestinations()

    @Serializable
    object AvatarDestination : AppNavDestinations()

    @Serializable
    data class ResultDestination(val videoId: String) : AppNavDestinations()

    @Serializable
    object TextPostDestination : AppNavDestinations()

    @Serializable
    object IdeasDestination : AppNavDestinations()

    @Serializable
    data class TranslateDestination(val videoId: String) : AppNavDestinations()

    @Serializable
    object HistoryDestination : AppNavDestinations()

    @Serializable
    object SettingsDestination : AppNavDestinations()

    @Serializable
    object TokensDestination : AppNavDestinations()
}
