package app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import data.repository.AppRepository
import di.Koin
import i18n.LocalStrings
import i18n.getDeviceLanguage
import i18n.stringsForLanguage
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ui.navigation.AppNavDestinations
import ui.navigation.AppNavGraph
import ui.navigation.AppNavigationActions
import ui.theme.VideoAvatarAITheme

@Composable
fun App() {
    VideoAvatarAITheme {
        // Определяем язык устройства → загружаем нужные строки
        val lang    = remember { getDeviceLanguage() }
        val strings = remember(lang) { stringsForLanguage(lang) }

        CompositionLocalProvider(LocalStrings provides strings) {
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
                        navController       = navController,
                        navigationActions   = navigationActions,
                        startDestination    = AppNavDestinations.LaunchDestination,
                        onCurrentRoute      = { currentRoute.value = it },
                    )
                }
            }
        }
    }
}
