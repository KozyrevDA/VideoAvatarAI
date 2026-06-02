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
import di.Koin
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ui.navigation.AppNavDestinations
import ui.navigation.AppNavGraph
import ui.navigation.AppNavigationActions
import ui.theme.VideoAvatarAITheme
import data.repository.AppRepository

@Composable
fun App() {
    VideoAvatarAITheme {
        KoinContext(context = Koin.koinConfiguration()) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                val navController = rememberNavController()
                val navigationActions = remember(navController) {
                    AppNavigationActions(navController)
                }
                val currentRoute: MutableState<AppNavDestinations> = remember {
                    mutableStateOf(AppNavDestinations.LaunchDestination)
                }
                val repository: AppRepository = koinInject()

                LaunchedEffect(Unit) {
                    if (!repository.sharedPrefs.isFirstOpen() &&
                        repository.sharedPrefs.isPastOnboarding()
                    ) {
                        navigationActions.navigateToHome(popUpTo = true)
                    } else {
                        navigationActions.navigateToOnboarding(popUpTo = true)
                    }
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
}
