package ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.navigation.AppNavigationActions
import ui.screens.home.BottomNav
import ui.theme.Background
import ui.theme.BorderSecondary
import ui.theme.OnBackground
import ui.theme.Primary
import ui.theme.PrimaryContainer
import ui.theme.Secondary
import ui.theme.SecondaryContainer
import ui.theme.TextSecondary
import ui.theme.TextTertiary

private data class HistoryEntry(
    val emoji: String,
    val bgColor: Color,
    val title: String,
    val subtitle: String,
)

private val mockHistory = listOf(
    HistoryEntry("👤", Color(0xFFEEEDFE), "Видео — паста рецепт", "Сегодня, 14:32 · Instagram"),
    HistoryEntry("🌍", Color(0xFFE1F5EE), "Перевод → English", "Сегодня, 14:35"),
    HistoryEntry("✏️", Color(0xFFEEEDFE), "Текст для поста", "Вчера, 10:20"),
    HistoryEntry("👤", Color(0xFFEEEDFE), "Видео — обзор кафе", "Вчера, 09:15 · TikTok"),
    HistoryEntry("🌍", Color(0xFFFEF3E2), "Перевод → Español", "3 дня назад"),
    HistoryEntry("💡", Color(0xFFEEEDFE), "30 идей постов", "4 дня назад"),
    HistoryEntry("👤", Color(0xFFEEEDFE), "Видео — утренняя рутина", "5 дней назад · Instagram"),
)

@Composable
fun HistoryScreen(navigationActions: AppNavigationActions) {
    Column(
        modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text("История", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
        ) {
            items(mockHistory) { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(entry.bgColor),
                        contentAlignment = Alignment.Center,
                    ) { Text(entry.emoji, fontSize = 20.sp) }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.title, fontSize = 12.sp, color = OnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(entry.subtitle, fontSize = 9.sp, color = TextTertiary)
                    }
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SecondaryContainer).padding(horizontal = 7.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("✓", fontSize = 8.sp, color = Secondary)
                        Text("Готово", fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderSecondary))
            }
            item { Spacer(Modifier.height(80.dp)) }
        }

        BottomNav(
            current = "history",
            onHome = { navigationActions.navigateToHome() },
            onHistory = {},
            onIdeas = { navigationActions.navigateToIdeas() },
            onSettings = { navigationActions.navigateToSettings() },
        )
    }
}
