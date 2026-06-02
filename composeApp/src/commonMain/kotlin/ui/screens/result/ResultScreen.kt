package ui.screens.result

import androidx.compose.animation.core.*
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
import org.koin.core.parameter.parametersOf
import ui.components.GreenButton
import ui.navigation.AppNavigationActions
import ui.theme.*

@Composable
fun ResultScreen(
    navigationActions: AppNavigationActions,
    videoId: String,
    viewModel: ResultViewModel = koinViewModel(parameters = { parametersOf(videoId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val infinite = rememberInfiniteTransition()
    val pulseAlpha by infinite.animateFloat(1f, 0.4f, infiniteRepeatable(tween(900), RepeatMode.Reverse))

    Column(
        modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()
            .padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer).clickable { navigationActions.navigateBack() }, contentAlignment = Alignment.Center) {
                Text("←", fontSize = 16.sp, color = Primary)
            }
            Text(if (uiState.isGenerating) "Создаём видео..." else "Видео готово!", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        }
        Spacer(Modifier.height(4.dp))
        Text(if (uiState.isGenerating) "~30 секунд" else "Твоё лицо. Твой голос.", fontSize = 12.sp, color = TextSecondary)
        Spacer(Modifier.height(14.dp))

        if (uiState.isGenerating) {
            GeneratingView(uiState, pulseAlpha)
        } else {
            ReadyView(
                onTranslate = { navigationActions.navigateToTranslate(videoId) },
                onAddText = { navigationActions.navigateToTextPost() },
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun GeneratingView(uiState: ResultUiState, pulseAlpha: Float) {
    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).border(1.5.dp, PrimaryContainerDark, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Surface), contentAlignment = Alignment.Center) {
                Text("👤", fontSize = 24.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(0.4f, 0.7f, 1f, 0.8f, 0.5f).forEach { h ->
                    Box(modifier = Modifier.width(3.dp).height((h * 16 * pulseAlpha).dp.coerceAtLeast(4.dp)).clip(RoundedCornerShape(2.dp)).background(Primary.copy(0.6f)))
                }
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    ProgressRow("Создаём аватар", uiState.progress1)
    Spacer(Modifier.height(8.dp))
    ProgressRow("Синхронизируем речь", uiState.progress2)
    Spacer(Modifier.height(8.dp))
    ProgressRow("Переводим на English", uiState.progress3)
    Spacer(Modifier.height(12.dp))
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PrimaryContainer).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(14.dp)).padding(12.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("✨", fontSize = 11.sp)
                Text("ИИ говорит", fontSize = 9.sp, color = Primary)
            }
            Spacer(Modifier.height(4.dp))
            Text("Отличное фото — аватар будет реалистичным", fontSize = 11.sp, color = Primary.copy(0.85f), lineHeight = 16.sp)
        }
    }
}

@Composable
private fun ProgressRow(label: String, progress: Float) {
    val isDone = progress >= 1f
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            if (isDone) {
                Row(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SecondaryContainer).padding(horizontal = 6.dp, vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✓", fontSize = 8.sp, color = Secondary)
                    Text("Готово", fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
                }
            } else {
                Text("${(progress * 100).toInt()}%", fontSize = 9.sp, color = Primary)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(5.dp)).background(BorderSecondary)) {
            Box(modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(5.dp).clip(RoundedCornerShape(5.dp)).background(if (isDone) Secondary else Primary))
        }
    }
}

@Composable
private fun ReadyView(onTranslate: () -> Unit, onAddText: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).border(1.5.dp, Primary.copy(0.3f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Surface), contentAlignment = Alignment.Center) {
            Text("▶", fontSize = 22.sp, color = Primary)
        }
        Row(modifier = Modifier.align(Alignment.TopStart).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            LangTag("RU", true); LangTag("EN", false)
        }
        Text("0:15", fontSize = 8.sp, color = TextSecondary, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).clip(RoundedCornerShape(4.dp)).background(Surface.copy(0.8f)).padding(horizontal = 4.dp, vertical = 2.dp))
    }
    Spacer(Modifier.height(10.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        listOf("40+" to "языков", "Твой" to "голос", "1 мин" to "на видео").forEach { (v, l) ->
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(14.dp)).padding(8.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(v, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                    Text(l, fontSize = 8.sp, color = TextTertiary)
                }
            }
        }
    }
    Spacer(Modifier.height(10.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        listOf("⬇", "↗", "↺").forEach { icon ->
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(10.dp)).padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 16.sp)
            }
        }
    }
    Spacer(Modifier.height(12.dp))

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(SecondaryContainer).border(0.5.dp, Secondary.copy(0.2f), RoundedCornerShape(14.dp)).padding(12.dp)) {
        Column {
            Text("Перевести на English — 1 токен", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF085041))
            Text("1.5 млрд человек говорят на английском", fontSize = 9.sp, color = Secondary.copy(0.7f))
        }
    }
    Spacer(Modifier.height(8.dp))
    GreenButton("Перевести", onClick = onTranslate)
    Spacer(Modifier.height(6.dp))
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(14.dp)).clickable { onAddText() }.padding(12.dp), contentAlignment = Alignment.Center) {
        Text("Добавить текст для поста", fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
private fun LangTag(lang: String, isPrimary: Boolean) {
    Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(if (isPrimary) Primary else SecondaryContainer).padding(horizontal = 5.dp, vertical = 2.dp)) {
        Text(lang, fontSize = 8.sp, color = if (isPrimary) Color.White else Secondary, fontWeight = FontWeight.Medium)
    }
}
