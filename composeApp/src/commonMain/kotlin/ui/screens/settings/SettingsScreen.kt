package ui.screens.settings

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ui.navigation.AppNavigationActions
import ui.screens.home.BottomNav
import ui.theme.*

@Composable
fun SettingsScreen(
    navigationActions: AppNavigationActions,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Text("Настройки", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            Spacer(Modifier.height(16.dp))

            // Profile
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(PrimaryContainer).border(1.5.dp, PrimaryContainerDark, CircleShape), contentAlignment = Alignment.Center) {
                    Text("МА", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Primary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Мой аккаунт", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                    Text("${if (uiState.isPro) "Pro" else "Базовый"} · ${uiState.tokensCount} токенов", fontSize = 11.sp, color = TextSecondary)
                }
                if (uiState.isPro) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("Pro", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Divider()

            SectionHeader("Подписка")
            SettingsRow("Текущий план") {
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(if (uiState.isPro) "Pro · 499₽/мес" else "Базовый", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Medium)
                }
            }
            Divider()
            SettingsRow("Токены") {
                Text("Купить ещё", fontSize = 12.sp, color = Primary, modifier = Modifier.clickable { navigationActions.navigateToTokens() })
            }

            Spacer(Modifier.height(8.dp))
            Divider()
            SectionHeader("Мой голос")
            SettingsRow("Клонирование голоса") {
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (uiState.voiceCloned) SecondaryContainer else BorderSecondary).padding(horizontal = 7.dp, vertical = 3.dp)) {
                    Text(if (uiState.voiceCloned) "✓ Записан" else "Не записан", fontSize = 9.sp, color = if (uiState.voiceCloned) Secondary else TextSecondary, fontWeight = FontWeight.Medium)
                }
            }
            Divider()
            SettingsRow("Записать голос", showArrow = true, onClick = {}) {}

            Spacer(Modifier.height(8.dp))
            Divider()
            SectionHeader("Приложение")
            SettingsRow("Уведомления") {
                Toggle(enabled = uiState.notificationsEnabled, onToggle = { viewModel.toggleNotifications() })
            }

            Spacer(Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth().clickable { viewModel.cancelSubscription() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Text("Отменить подписку", fontSize = 13.sp, color = Error)
            }
            Spacer(Modifier.height(80.dp))
        }

        BottomNav("settings",
            onHome = { navigationActions.navigateToHome() },
            onHistory = { navigationActions.navigateToHistory() },
            onIdeas = { navigationActions.navigateToIdeas() },
            onSettings = {},
        )
    }
}

@Composable private fun SectionHeader(title: String) {
    Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary, modifier = Modifier.padding(vertical = 10.dp))
}

@Composable private fun Divider() {
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderSecondary))
}

@Composable private fun SettingsRow(label: String, showArrow: Boolean = false, onClick: (() -> Unit)? = null, trailing: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier).padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 13.sp, color = OnBackground)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            trailing()
            if (showArrow) Text("→", fontSize = 14.sp, color = TextTertiary)
        }
    }
}

@Composable private fun Toggle(enabled: Boolean, onToggle: () -> Unit) {
    Box(modifier = Modifier.size(width = 38.dp, height = 22.dp).clip(RoundedCornerShape(11.dp)).background(if (enabled) Primary else BorderSecondary).clickable { onToggle() }, contentAlignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart) {
        Box(modifier = Modifier.padding(3.dp).size(16.dp).clip(CircleShape).background(Color.White))
    }
}
