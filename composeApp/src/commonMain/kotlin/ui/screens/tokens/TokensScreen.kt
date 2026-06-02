package ui.screens.tokens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import ui.components.PrimaryButton
import ui.navigation.AppNavigationActions
import ui.theme.*

private data class Pack(val count: Int, val price: Int, val note: String, val popular: Boolean = false)
private val packs = listOf(Pack(1, 80, "попробуй ещё раз"), Pack(5, 400, "80 ₽ за видео"), Pack(10, 800, "80 ₽ за видео · Выгодно", popular = true))

@Composable
fun TokensScreen(
    navigationActions: AppNavigationActions,
    viewModel: TokensViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isPurchaseSuccess) {
        if (uiState.isPurchaseSuccess) navigationActions.navigateBack()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(WarningContainer), contentAlignment = Alignment.Center) {
            Text("🪙", fontSize = 26.sp)
        }
        Spacer(Modifier.height(10.dp))
        Text("Токены закончились", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        Spacer(Modifier.height(4.dp))
        Text("Пополни чтобы продолжить создавать видео", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            packs.forEachIndexed { idx, pack ->
                val selected = uiState.selectedPack == idx
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(if (selected) PrimaryContainer else Surface).border(if (selected) 1.5.dp else 0.5.dp, if (selected) Primary else BorderSecondary, RoundedCornerShape(16.dp)).clickable { viewModel.selectPack(idx) }.padding(16.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${pack.count} видео", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (selected) Primary else OnBackground)
                                if (pack.popular) {
                                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(WarningContainer).padding(horizontal = 7.dp, vertical = 2.dp)) {
                                        Text("Выгодно", fontSize = 8.sp, color = Warning, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                            Text(pack.note, fontSize = 11.sp, color = if (selected) Primary.copy(0.7f) else TextSecondary)
                        }
                        Text("${pack.price} ₽", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = if (selected) Primary else OnBackground)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            } else {
                PrimaryButton("Купить ${packs[uiState.selectedPack].count} видео", onClick = { viewModel.purchase() })
            }
            Spacer(Modifier.height(6.dp))
            Text("Списание через привязанную карту", fontSize = 10.sp, color = TextTertiary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(32.dp))
    }
}
