package ui.screens.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import ui.navigation.AppNavigationActions
import ui.screens.home.BottomNav
import ui.theme.Background
import ui.theme.BorderSecondary
import ui.theme.Error
import ui.theme.OnBackground
import ui.theme.Primary
import ui.theme.PrimaryContainer
import ui.theme.PrimaryContainerDark
import ui.theme.Secondary
import ui.theme.SecondaryContainer
import ui.theme.Surface
import ui.theme.TextSecondary
import ui.theme.TextTertiary

@Composable
fun SettingsScreen(navigationActions: AppNavigationActions) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Настройки", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            Spacer(Modifier.height(16.dp))

            // User profile
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(PrimaryContainer).border(1.5.dp, PrimaryContainerDark, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text("МА", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Primary) }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Маша Алексеева", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                    Text("Pro · 27 токенов", fontSize = 11.sp, color = TextSecondary)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).padding(horizontal = 8.dp, vertical = 4.dp)
                ) { Text("Pro", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Medium) }
            }

            Spacer(Modifier.height(16.dp))
            Divider()

            SectionHeader("Подписка")
            SettingsRow("Текущий план") {
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("Годовой · 2490₽", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Medium)
                }
            }
            Divider()
            SettingsRow("Следующий платёж") { Text("15 июн 2027", fontSize = 12.sp, color = TextSecondary) }
            Divider()
            SettingsRow("Токены") {
                Text("Купить ещё", fontSize = 12.sp, color = Primary, modifier = Modifier.clickable { navigationActions.navigateToTokens() })
            }

            Spacer(Modifier.height(8.dp))
            Divider()

            SectionHeader("Мой голос")
            SettingsRow("Голос записан") {
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SecondaryContainer).padding(horizontal = 7.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("✓", fontSize = 9.sp, color = Secondary)
                    Text("Активен", fontSize = 9.sp, color = Secondary, fontWeight = FontWeight.Medium)
                }
            }
            Divider()
            SettingsRow("Перезаписать голос", showArrow = true, onClick = {}) { }

            Spacer(Modifier.height(8.dp))
            Divider()

            SectionHeader("Приложение")
            SettingsRow("Уведомления") {
                Toggle(enabled = notificationsEnabled, onToggle = { notificationsEnabled = !notificationsEnabled })
            }

            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) { Text("Отменить подписку", fontSize = 13.sp, color = Error) }

            Spacer(Modifier.height(80.dp))
        }

        BottomNav(
            current = "settings",
            onHome = { navigationActions.navigateToHome() },
            onHistory = { navigationActions.navigateToHistory() },
            onIdeas = { navigationActions.navigateToIdeas() },
            onSettings = {},
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary,
        modifier = Modifier.padding(vertical = 10.dp),
    )
}

@Composable
private fun SettingsRow(
    label: String,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 13.sp, color = OnBackground)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            trailing()
            if (showArrow) Text("→", fontSize = 14.sp, color = TextTertiary)
        }
    }
}

@Composable
private fun Divider() {
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderSecondary))
}

@Composable
private fun Toggle(enabled: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 38.dp, height = 22.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (enabled) Primary else BorderSecondary)
            .clickable { onToggle() },
        contentAlignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier.padding(3.dp).size(16.dp).clip(CircleShape).background(Color.White)
        )
    }
}
