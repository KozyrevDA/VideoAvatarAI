package ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ui.screens.avatar.AvatarScreen
import ui.screens.history.HistoryScreen
import ui.screens.home.HomeScreen
import ui.screens.ideas.IdeasScreen
import ui.screens.launch.LaunchScreen
import ui.screens.onboarding.OnboardingScreen
import ui.screens.paywall.PaywallScreen
import ui.screens.result.ResultScreen
import ui.screens.settings.SettingsScreen
import ui.screens.textpost.TextPostScreen
import ui.screens.tokens.TokensScreen
import ui.screens.translate.TranslateScreen

private const val ANIM_DURATION = 200

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigationActions: AppNavigationActions,
    startDestination: AppNavDestinations,
    onCurrentRoute: (AppNavDestinations) -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(tween(ANIM_DURATION), initialAlpha = 0.9f) },
        exitTransition = { fadeOut(tween(ANIM_DURATION)) },
        popEnterTransition = { fadeIn(tween(ANIM_DURATION), initialAlpha = 0.9f) },
        popExitTransition = { fadeOut(tween(ANIM_DURATION)) },
    ) {

        composable<AppNavDestinations.LaunchDestination> {
            onCurrentRoute(AppNavDestinations.LaunchDestination)
            LaunchScreen()
        }

        composable<AppNavDestinations.OnboardingDestination> {
            onCurrentRoute(AppNavDestinations.OnboardingDestination)
            Surface(color = MaterialTheme.colorScheme.background) {
                OnboardingScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.PaywallDestination> { backEntry ->
            val dest = backEntry.toRoute<AppNavDestinations.PaywallDestination>()
            onCurrentRoute(AppNavDestinations.PaywallDestination(dest.prevScreen))
            Surface(color = MaterialTheme.colorScheme.background) {
                PaywallScreen(
                    navigationActions = navigationActions,
                    prevScreen = dest.prevScreen,
                )
            }
        }

        composable<AppNavDestinations.HomeDestination> {
            onCurrentRoute(AppNavDestinations.HomeDestination)
            Surface(color = MaterialTheme.colorScheme.background) {
                HomeScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.AvatarDestination> {
            onCurrentRoute(AppNavDestinations.AvatarDestination)
            Surface(color = MaterialTheme.colorScheme.background) {
                AvatarScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.ResultDestination> { backEntry ->
            val dest = backEntry.toRoute<AppNavDestinations.ResultDestination>()
            onCurrentRoute(AppNavDestinations.ResultDestination(dest.videoId))
            Surface(color = MaterialTheme.colorScheme.background) {
                ResultScreen(
                    navigationActions = navigationActions,
                    videoId = dest.videoId,
                )
            }
        }

        composable<AppNavDestinations.TextPostDestination> {
            onCurrentRoute(AppNavDestinations.TextPostDestination)
            Surface(color = MaterialTheme.colorScheme.background) {
                TextPostScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.IdeasDestination> {
            onCurrentRoute(AppNavDestinations.IdeasDestination)
            Surface(color = MaterialTheme.colorScheme.background) {
                IdeasScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.TranslateDestination> { backEntry ->
            val dest = backEntry.toRoute<AppNavDestinations.TranslateDestination>()
            onCurrentRoute(AppNavDestinations.TranslateDestination(dest.videoId))
            Surface(color = MaterialTheme.colorScheme.background) {
                TranslateScreen(
                    navigationActions = navigationActions,
                    videoId = dest.videoId,
                )
            }
        }

        composable<AppNavDestinations.HistoryDestination> {
            onCurrentRoute(AppNavDestinations.HistoryDestination)
            Surface(color = MaterialTheme.colorScheme.background) {
                HistoryScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.SettingsDestination> {
            onCurrentRoute(AppNavDestinations.SettingsDestination)
            Surface(color = MaterialTheme.colorScheme.background) {
                SettingsScreen(navigationActions = navigationActions)
            }
        }

        composable<AppNavDestinations.TokensDestination> {
            onCurrentRoute(AppNavDestinations.TokensDestination)
            Surface(color = MaterialTheme.colorScheme.background) {
                TokensScreen(navigationActions = navigationActions)
            }
        }
    }
}
