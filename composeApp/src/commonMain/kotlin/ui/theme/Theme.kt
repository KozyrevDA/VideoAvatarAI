package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    secondary = Secondary,
    secondaryContainer = SecondaryContainer,
    background = Background,
    surface = Surface,
    onBackground = OnBackground,
    onSurface = OnBackground,
    error = Error,
)

@Composable
fun VideoAvatarAITheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
