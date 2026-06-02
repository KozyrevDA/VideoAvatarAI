package ui.screens.translate

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.model.languages
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ui.navigation.AppNavigationActions
import ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TranslateScreen(
    navigationActions: AppNavigationActions,
    videoId: String,
    viewModel: TranslateViewModel = koinViewModel(parameters = { parametersOf(videoId) }),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer).clickable { navigationActions.navigateBack() }, contentAlignment = Alignment.Center) {
                    Text("←", fontSize = 16.sp, color = Primary)
                }
                Text("Перевести видео", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            }
            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp)).background(PrimaryContainer).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Text("▶", fontSize = 24.sp, color = Primary)
                Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).clip(RoundedCornerShape(5.dp)).background(Primary).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("RU — исходное", fontSize = 8.sp, color = Color.White)
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("На какой язык перевести?", fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                languages.forEach { lang ->
                    val sel = uiState.selectedLanguage == lang
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (sel) PrimaryContainer else Surface).border(0.5.dp, if (sel) PrimaryContainerDark else BorderSecondary, RoundedCornerShape(20.dp)).clickable { viewModel.onLanguageSelect(lang) }.padding(horizontal = 10.dp, vertical = 5.dp)) {
                        Text(if (sel) "$lang ✓" else lang, fontSize = 10.sp, color = if (sel) Primary else TextSecondary)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("Голос", fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(true to "Мой голос", false to "Стандартный").forEach { (mine, label) ->
                    val sel = uiState.useClonedVoice == mine
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (sel) PrimaryContainer else Surface).border(0.5.dp, if (sel) PrimaryContainerDark else BorderSecondary, RoundedCornerShape(20.dp)).clickable { viewModel.onVoiceToggle(mine) }.padding(horizontal = 10.dp, vertical = 5.dp)) {
                        Text(if (sel) "$label ✓" else label, fontSize = 10.sp, color = if (sel) Primary else TextSecondary)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PrimaryContainer).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(12.dp)).padding(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 12.sp)
                    Text(when (uiState.selectedLanguage) {
                        "English" -> "English аудитория — 1.5 млрд человек"
                        "Español" -> "Spanish аудитория — 500 млн человек"
                        "中文" -> "Chinese аудитория — 1.1 млрд человек"
                        else -> "${uiState.selectedLanguage} — миллионы новых зрителей"
                    }, fontSize = 11.sp, color = Primary.copy(0.85f))
                }
            }
            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Токены", fontSize = 11.sp, color = TextSecondary)
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(WarningContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("${uiState.tokensCount} осталось", fontSize = 9.sp, color = Warning, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Secondary) }
            } else {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Secondary).clickable { viewModel.translate { taskId -> navigationActions.navigateToResult(taskId) } }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text("Перевести — 1 токен", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}
