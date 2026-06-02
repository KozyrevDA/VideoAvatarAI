package ui.screens.tokens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.components.PrimaryButton
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

private data class TokenPackUI(val count: Int, val price: Int, val label: String, val isPopular: Boolean = false)

private val packs = listOf(
    TokenPackUI(1, 80, "80 ₽"),
    TokenPackUI(5, 400, "400 ₽"),
    TokenPackUI(10, 800, "800 ₽", isPopular = true),
)

@Composable
fun TokensScreen(navigationActions: AppNavigationActions) {
    var selectedPack by remember { mutableStateOf(2) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(WarningContainer),
            contentAlignment = Alignment.Center,
        ) { Text("🪙", fontSize = 26.sp) }
        Spacer(Modifier.height(10.dp))
        Text("Токены закончились", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        Spacer(Modifier.height(4.dp))
        Text(
            "Пополни чтобы продолжить создавать видео",
            fontSize = 13.sp, color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Spacer(Modifier.height(24.dp))

        // Token packs
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            packs.forEachIndexed { idx, pack ->
                val selected = selectedPack == idx
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) PrimaryContainer else Surface)
                        .border(
                            if (selected) 1.5.dp else 0.5.dp,
                            if (selected) Primary else BorderSecondary,
                            RoundedCornerShape(16.dp),
                        )
                        .clickable { selectedPack = idx }
                        .padding(16.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "${pack.count} ${if (pack.count == 1) "видео" else "видео"}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selected) Primary else OnBackground,
                                )
                                if (pack.isPopular) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Warning.copy(0.15f))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) { Text("Выгодно", fontSize = 8.sp, color = Warning, fontWeight = FontWeight.Medium) }
                                }
                            }
                            Text(
                                "80 ₽ за видео",
                                fontSize = 11.sp,
                                color = if (selected) Primary.copy(0.7f) else TextSecondary,
                            )
                        }
                        Text(
                            pack.label,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selected) Primary else OnBackground,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
            PrimaryButton(
                text = "Купить ${packs[selectedPack].count} видео",
                onClick = { navigationActions.navigateBack() },
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Списание через привязанную карту",
                fontSize = 10.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}
