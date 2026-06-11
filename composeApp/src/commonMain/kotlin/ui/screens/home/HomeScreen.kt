package ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import i18n.LocalStrings
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ui.navigation.AppNavigationActions
import ui.theme.*

@Composable
fun HomeScreen(
    navigationActions: AppNavigationActions,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val s = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(16.dp))

            // Header
            AnimatedVisibility(visible, enter = fadeIn(tween(400))) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(s.homeGreeting, fontSize = 11.sp, color = TextTertiary)
                        Text("Привет!", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(20.dp)).clickable { navigationActions.navigateToTokens() }.padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${uiState.tokensCount}", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.Medium)
                                Text("🪙", fontSize = 12.sp)
                            }
                        }
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(PrimaryContainer).border(1.5.dp, PrimaryContainerDark, CircleShape).clickable { navigationActions.navigateToSettings() }, contentAlignment = Alignment.Center) {
                            Text(if (uiState.isPro) "Pro" else "Ты", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Primary)
                        }
                    }
                }
            }
            Spacer(Modifier.height(18.dp))

            // Bento grid
            AnimatedVisibility(visible, enter = slideInVertically(tween(400)) { -it }) {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    // Main: Video from photo
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(GradientPurpleStart, GradientPurpleEnd))).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(20.dp)).clickable { navigationActions.navigateToAvatar() }.padding(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Primary.copy(0.18f)), contentAlignment = Alignment.Center) { Text("👤", fontSize = 22.sp) }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(s.avatarTitle, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF26215C), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(s.avatarSubtitle, fontSize = 11.sp, color = Primary.copy(0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Surface), contentAlignment = Alignment.Center) { Text("→", fontSize = 14.sp, color = Primary) }
                        }
                    }

                    // Small cards
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        SmallCard("✏️", "Текст", "для поста", Modifier.weight(1f)) { navigationActions.navigateToTextPost() }
                        SmallCard("💡", s.navIdeas, "30 идей", Modifier.weight(1f)) { navigationActions.navigateToIdeas() }
                    }

                    // Translation
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(GradientGreenStart, GradientGreenEnd))).border(0.5.dp, SecondaryBorder, RoundedCornerShape(20.dp)).clickable { navigationActions.navigateToTranslate("") }.padding(14.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(SecondaryContainer), contentAlignment = Alignment.Center) { Text("🌍", fontSize = 19.sp) }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(s.translateTitle, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF085041))
                                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SecondaryContainer).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text("Новинка", fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Text("Твой голос на 40+ языках", fontSize = 11.sp, color = Secondary.copy(0.7f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                LangBadge("RU"); Text("↓", fontSize = 8.sp, color = TextTertiary); LangBadge("EN")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Recent
            AnimatedVisibility(visible, enter = fadeIn(tween(600))) {
                Column {
                    Text("ПОСЛЕДНИЕ", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = TextTertiary, letterSpacing = 0.6.sp)
                    Spacer(Modifier.height(8.dp))
                    if (uiState.recentItems.isEmpty()) {
                        // Demo items when empty
                        HistoryItem("👤", PrimaryContainer, "Видео — паста рецепт", "2 мин · Instagram") {}
                        Spacer(Modifier.height(1.dp))
                        HistoryItem("🌍", SecondaryContainer, "Перевод → English", "Вчера · 14:35") {}
                    } else {
                        uiState.recentItems.forEach { item ->
                            HistoryItem("👤", PrimaryContainer, item.title, item.createdAt) { navigationActions.navigateToResult(item.id) }
                        }
                    }
                }
            }
            Spacer(Modifier.height(80.dp))
        }

        BottomNav("home",
            onHome = {},
            onHistory = { navigationActions.navigateToHistory() },
            onIdeas = { navigationActions.navigateToIdeas() },
            onSettings = { navigationActions.navigateToSettings() },
        )
    }
}

@Composable
private fun SmallCard(emoji: String, title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.clip(RoundedCornerShape(18.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(18.dp)).clickable { onClick() }.padding(12.dp)) {
        Column {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 16.sp) }
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = OnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, fontSize = 9.sp, color = TextTertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun HistoryItem(emoji: String, bg: Color, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(bg), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 18.sp) }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, color = OnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, fontSize = 9.sp, color = TextTertiary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SecondaryContainer).padding(horizontal = 8.dp, vertical = 3.dp)) {
            Text("✓", fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
            Text("Готово", fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun LangBadge(lang: String) {
    Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(SecondaryContainer).padding(horizontal = 5.dp, vertical = 1.dp)) {
        Text(lang, fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BottomNav(current: String, onHome: () -> Unit, onHistory: () -> Unit, onIdeas: () -> Unit, onSettings: () -> Unit) {
    val s = i18n.LocalStrings.current
    Row(modifier = Modifier.fillMaxWidth().background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(0.dp)).padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf(Triple("home","⌂",s.navHome), Triple("history","◷",s.navHistory), Triple("ideas","💡",s.navIdeas), Triple("settings","⚙",s.navSettings)).zip(listOf(onHome, onHistory, onIdeas, onSettings)).forEach { (item, action) ->
            val (key, icon, label) = item
            val sel = current == key
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { action() }.padding(horizontal = 14.dp, vertical = 4.dp)) {
                Text(icon, fontSize = 18.sp, color = if (sel) Primary else TextTertiary)
                Spacer(Modifier.height(2.dp))
                Text(label, fontSize = 8.sp, color = if (sel) Primary else TextTertiary, fontWeight = if (sel) FontWeight.Medium else FontWeight.Normal)
            }
        }
    }
}
