package ui.screens.avatar

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.components.PrimaryButton
import ui.components.SectionTitle
import ui.navigation.AppNavigationActions
import ui.theme.Background
import ui.theme.BorderSecondary
import ui.theme.OnBackground
import ui.theme.Primary
import ui.theme.PrimaryContainer
import ui.theme.PrimaryContainerDark
import ui.theme.Surface
import ui.theme.TextSecondary
import ui.theme.TextTertiary
import ui.theme.Warning
import ui.theme.WarningContainer

private val styles = listOf(
    Triple("💼", "Деловой", "BUSINESS"),
    Triple("😊", "Casual", "CASUAL"),
    Triple("⭐", "Эксперт", "EXPERT"),
)

private val platformsList = listOf("Instagram", "TikTok", "VK", "YouTube")

@Composable
fun AvatarScreen(navigationActions: AppNavigationActions) {
    var avatarText by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(0) }
    var selectedPlatform by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            // Back + title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryContainer)
                        .clickable { navigationActions.navigateBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("←", fontSize = 16.sp, color = Primary)
                }
                Text("Видео из фото", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            }
            Spacer(Modifier.height(18.dp))

            // Photo upload
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PrimaryContainer)
                    .border(1.5.dp, PrimaryContainerDark, RoundedCornerShape(20.dp))
                    .clickable { },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👤", fontSize = 28.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Загрузи своё фото", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium)
                    Text("или выбери готовый аватар", fontSize = 10.sp, color = TextSecondary)
                }
            }
            Spacer(Modifier.height(16.dp))

            // Text input
            SectionTitle("ЧТО СКАЖЕТ АВАТАР")
            Spacer(Modifier.height(6.dp))
            TextField(
                value = avatarText,
                onValueChange = { avatarText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderSecondary, RoundedCornerShape(14.dp)),
                placeholder = { Text("Привет! Сегодня покажу рецепт...", fontSize = 12.sp, color = TextTertiary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                minLines = 3,
            )
            Spacer(Modifier.height(16.dp))

            // Style
            SectionTitle("СТИЛЬ")
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                styles.forEachIndexed { idx, (emoji, label, _) ->
                    val selected = selectedStyle == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) PrimaryContainer else Surface)
                            .border(
                                if (selected) 1.5.dp else 0.5.dp,
                                if (selected) Primary else BorderSecondary,
                                RoundedCornerShape(14.dp),
                            )
                            .clickable { selectedStyle = idx },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 16.sp)
                            Text(label, fontSize = 9.sp, color = if (selected) Primary else TextSecondary)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // Platform + tokens
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    platformsList.forEachIndexed { idx, platform ->
                        val selected = selectedPlatform == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) PrimaryContainer else Color.Transparent)
                                .border(
                                    0.5.dp,
                                    if (selected) PrimaryContainerDark else BorderSecondary,
                                    RoundedCornerShape(20.dp),
                                )
                                .clickable { selectedPlatform = idx }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                if (selected) "$platform ✓" else platform,
                                fontSize = 9.sp,
                                color = if (selected) Primary else TextSecondary,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(WarningContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text("27 токенов", fontSize = 9.sp, color = Warning, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Bottom button
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            PrimaryButton(
                text = "Создать — 1 токен",
                onClick = { navigationActions.navigateToResult("demo_id") },
            )
        }
    }
}
