package ui.screens.paywall

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
import i18n.LocalStrings
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.model.subscriptionPlans
import org.koin.compose.viewmodel.koinViewModel
import platform.getBillingLauncher
import ui.components.PrimaryButton
import ui.navigation.AppNavigationActions
import ui.theme.*

@Composable
fun PaywallScreen(
    navigationActions: AppNavigationActions,
    source: String = "upgrade",
    viewModel: PaywallViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val billingLauncher = getBillingLauncher()

    LaunchedEffect(uiState.isPurchaseSuccess) {
        if (uiState.isPurchaseSuccess) {
            navigationActions.navigateToHome()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(20.dp))

            if (source != "onboarding") {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer)
                    .clickable { navigationActions.navigateBack() }, contentAlignment = Alignment.Center) {
                    Text("←", fontSize = 16.sp, color = Primary)
                }
                Spacer(Modifier.height(16.dp))
            }

            Text("Нейросеть Видео Pro", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OnBackground, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text("Всё для твоих соцсетей", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))

            // Фичи
            listOf("🎬  Видео-аватар — 5 в месяц", "✍️  Тексты постов — безлимит",
                   "🌍  Перевод на 40+ языков", "💡  30 идей контента", "📱  Instagram, TikTok, VK").forEach { feat ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
                    Box(modifier = Modifier.size(22.dp).clip(RoundedCornerShape(6.dp)).background(SecondaryContainer), contentAlignment = Alignment.Center) {
                        Text("✓", fontSize = 11.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(feat, fontSize = 13.sp, color = OnBackground)
                }
            }
            Spacer(Modifier.height(24.dp))

            // Планы
            subscriptionPlans.forEach { plan ->
                val sel = uiState.selectedPlan == plan.type
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (sel) PrimaryContainer else Surface)
                        .border(if (sel) 2.dp else 0.5.dp, if (sel) Primary else BorderSecondary, RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectPlan(plan.type) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(if (plan.type == "yearly") "Год" else "Месяц", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (sel) Primary else OnBackground)
                            if (plan.discount.isNotBlank()) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Secondary).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text(plan.discount, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(if (plan.type == "yearly") "Первые 7 дней — 1 ₽" else "Отмена в любой момент", fontSize = 11.sp, color = TextTertiary)
                    }
                    Text(plan.priceLabel, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (sel) Primary else OnBackground)
                }
            }
            Spacer(Modifier.height(8.dp))

            if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFFFEBEE)).padding(10.dp)) {
                    Text(uiState.error!!, fontSize = 12.sp, color = Color(0xFFC62828), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(8.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                PrimaryButton("Начать за 1 ₽ → ${if (uiState.selectedPlan == "yearly") "2 490 ₽/год" else "499 ₽/мес"}") {
                    viewModel.purchase { productId, onResult ->
                        billingLauncher?.launch(productId, onResult) ?: onResult(false, null)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Отмена подписки — в настройках магазина в любой момент", fontSize = 10.sp, color = TextTertiary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}
