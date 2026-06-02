package app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import ui.navigation.AppNavDestinations
import ui.navigation.AppNavGraph
import ui.navigation.AppNavigationActions
import ui.theme.VideoAvatarAITheme

@Composable
fun App() {
    VideoAvatarAITheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val navController = rememberNavController()
            val navigationActions = remember(navController) { AppNavigationActions(navController) }
            val currentRoute: MutableState<AppNavDestinations> = remember {
                mutableStateOf(AppNavDestinations.LaunchDestination)
            }

            LaunchedEffect(Unit) {
                // TODO: check SharedPrefs — если первый запуск → онбординг, иначе → home
                navigationActions.navigateToOnboarding(popUpTo = true)
            }

            AppNavGraph(
                navController = navController,
                navigationActions = navigationActions,
                startDestination = AppNavDestinations.LaunchDestination,
                onCurrentRoute = { currentRoute.value = it },
            )
        }
    }
}
