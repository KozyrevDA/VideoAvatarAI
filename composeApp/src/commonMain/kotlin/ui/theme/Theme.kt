package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary              = Primary,
    onPrimary            = OnPrimary,
    primaryContainer     = PrimaryContainer,
    onPrimaryContainer   = OnBackground,
    secondary            = Secondary,
    onSecondary          = OnPrimary,
    secondaryContainer   = SecondaryContainer,
    onSecondaryContainer = OnBackground,
    background           = Background,
    onBackground         = OnBackground,
    surface              = Surface,
    onSurface            = OnBackground,
    surfaceVariant       = SurfaceVariant,
    onSurfaceVariant     = TextSecondary,
    outline              = BorderPrimary,
    error                = Error,
)

@Composable
fun VideoAvatarAITheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content,
    )
}
