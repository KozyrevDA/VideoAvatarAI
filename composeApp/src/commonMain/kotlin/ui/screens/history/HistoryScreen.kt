package ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ui.navigation.AppNavigationActions
import ui.screens.home.BottomNav
import ui.theme.*

@Composable
fun HistoryScreen(
    navigationActions: AppNavigationActions,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text("История", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        }

        when {
            uiState.isLoading -> Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            uiState.items.isEmpty() -> LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                items(demoHistory) { item ->
                    HistoryRow(
                        emoji    = item.emoji,
                        bg       = item.bg,
                        title    = item.title,
                        subtitle = item.subtitle,
                        status   = "ready",
                        onClick  = {},  // демо — нет реального ID
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
            else -> LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                items(uiState.items) { item ->
                    val emoji = when (item.type) {
                        "translation" -> "🌍"
                        "text_post"   -> "✏️"
                        "ideas"       -> "💡"
                        else          -> "👤"  // avatar
                    }
                    val bg = when (item.type) {
                        "translation" -> SecondaryContainer
                        else          -> PrimaryContainer
                    }
                    HistoryRow(
                        emoji    = emoji,
                        bg       = bg,
                        title    = item.title,
                        subtitle = item.createdAt,
                        status   = item.status,
                        onClick  = {
                            if (item.status == "ready") {
                                navigationActions.navigateToResult(item.id)
                            }
                        },
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        BottomNav("history",
            onHome     = { navigationActions.navigateToHome() },
            onHistory  = {},
            onIdeas    = { navigationActions.navigateToIdeas() },
            onSettings = { navigationActions.navigateToSettings() },
        )
    }
}

private data class DemoItem(val emoji: String, val bg: Color, val title: String, val subtitle: String)
private val demoHistory = listOf(
    DemoItem("👤", Color(0xFFEEEDFE), "Видео — паста рецепт",  "Сегодня, 14:32 · Instagram"),
    DemoItem("🌍", Color(0xFFE1F5EE), "Перевод → English",     "Сегодня, 14:35"),
    DemoItem("✏️", Color(0xFFEEEDFE), "Текст для поста",       "Вчера, 10:20"),
    DemoItem("👤", Color(0xFFEEEDFE), "Видео — обзор кафе",   "Вчера, 09:15 · TikTok"),
    DemoItem("💡", Color(0xFFEEEDFE), "30 идей постов",        "4 дня назад"),
)

@Composable
private fun HistoryRow(emoji: String, bg: Color, title: String, subtitle: String, status: String, onClick: () -> Unit) {
    val isReady = status == "ready"
    Row(
        modifier = Modifier.fillMaxWidth()
            .then(if (isReady) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(bg), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, color = OnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, fontSize = 9.sp, color = TextTertiary)
        }
        when (status) {
            "ready" -> Row(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SecondaryContainer).padding(horizontal = 7.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("✓", fontSize = 8.sp, color = Secondary)
                Text("Готово", fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
            }
            "processing" -> Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(WarningContainer).padding(horizontal = 7.dp, vertical = 3.dp)) {
                Text("⏳ Создаётся", fontSize = 8.sp, color = Warning, fontWeight = FontWeight.Medium)
            }
            else -> Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFFFFEBEE)).padding(horizontal = 7.dp, vertical = 3.dp)) {
                Text("Ошибка", fontSize = 8.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Medium)
            }
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderSecondary))
}
