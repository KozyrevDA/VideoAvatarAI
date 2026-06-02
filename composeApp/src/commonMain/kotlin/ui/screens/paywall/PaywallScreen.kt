package ui.screens.paywall

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ui.components.CardBase
import ui.components.PrimaryButton
import ui.navigation.AppNavigationActions
import ui.theme.*

@Composable
fun PaywallScreen(
    navigationActions: AppNavigationActions,
    prevScreen: String,
    viewModel: PaywallViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(uiState.isPurchaseSuccess) {
        if (uiState.isPurchaseSuccess) {
            navigationActions.navigateToHome(popUpTo = true)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()
            .padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))

        AnimatedVisibility(visible, enter = fadeIn()) {
            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                Text("👑", fontSize = 28.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        AnimatedVisibility(visible, enter = fadeIn()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Нейросеть Видео Pro", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                Spacer(Modifier.height(4.dp))
                Text("Всё для твоих соцсетей", fontSize = 12.sp, color = TextSecondary)
            }
        }
        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(visible, enter = fadeIn()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("40+" to "языков", "5" to "видео/мес", "∞" to "тексты").forEach { (v, l) ->
                    CardBase(modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(v, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                            Spacer(Modifier.height(2.dp))
                            Text(l, fontSize = 9.sp, color = TextTertiary)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible, enter = slideInVertically { it }) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(PrimaryContainer).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(18.dp)).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(
                    "Видео из фото — 5 в мес + токены",
                    "Текст для поста — безлимит",
                    "Перевод на 40+ языков",
                    "Придумай темы — 30 идей",
                    "Форматы для всех платформ",
                ).forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(18.dp).clip(RoundedCornerShape(50)).background(SecondaryContainer), contentAlignment = Alignment.Center) {
                            Text("✓", fontSize = 9.sp, color = Secondary, fontWeight = FontWeight.Medium)
                        }
                        Text(feature, fontSize = 12.sp, color = Primary)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible, enter = fadeIn()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                val monthSel = uiState.selectedPlan == "monthly"
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).background(if (monthSel) PrimaryContainer else Surface).border(if (monthSel) 1.5.dp else 0.5.dp, if (monthSel) Primary else BorderSecondary, RoundedCornerShape(18.dp)).clickable { viewModel.selectPlan("monthly") }.padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("499 ₽", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (monthSel) Primary else OnBackground)
                        Text("/месяц", fontSize = 9.sp, color = TextSecondary)
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    val yearSel = uiState.selectedPlan == "yearly"
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(if (yearSel) PrimaryContainer else Surface).border(if (yearSel) 1.5.dp else 0.5.dp, if (yearSel) Primary else BorderSecondary, RoundedCornerShape(18.dp)).clickable { viewModel.selectPlan("yearly") }.padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("2 490 ₽", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (yearSel) Primary else OnBackground)
                            Text("/год", fontSize = 9.sp, color = TextSecondary)
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.TopCenter).clip(RoundedCornerShape(20.dp)).background(Secondary).padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text("−58%", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible, enter = fadeIn()) {
            Column {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else {
                    PrimaryButton(text = "Начать за 1 ₽", onClick = {
                        if (uiState.selectedPlan == "yearly") viewModel.purchaseYearly()
                        else viewModel.purchaseMonthly()
                    })
                }
                Spacer(Modifier.height(6.dp))
                Text("Через 7 дней 2 490₽/год · Отмена в любой момент", fontSize = 10.sp, color = TextTertiary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(4.dp))
                Text("Доп. видео — 80 ₽ за штуку", fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
