package ui.screens.result

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import kotlinx.coroutines.delay
import ui.components.GreenButton
import ui.components.PrimaryButton
import ui.navigation.AppNavigationActions
import ui.theme.Background
import ui.theme.BorderSecondary
import ui.theme.GradientPurpleEnd
import ui.theme.GradientPurpleStart
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
fun ResultScreen(
    navigationActions: AppNavigationActions,
    videoId: String,
) {
    var isGenerating by remember { mutableStateOf(true) }
    var progress1 by remember { mutableFloatStateOf(1f) }
    var progress2 by remember { mutableFloatStateOf(0.3f) }
    var progress3 by remember { mutableFloatStateOf(0f) }

    // Simulate generation progress
    LaunchedEffect(Unit) {
        delay(800)
        progress2 = 0.65f
        delay(1000)
        progress2 = 1f
        progress3 = 0.3f
        delay(1000)
        progress3 = 1f
        delay(500)
        isGenerating = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer).clickable { navigationActions.navigateBack() },
                contentAlignment = Alignment.Center,
            ) { Text("←", fontSize = 16.sp, color = Primary) }
            Text(
                if (isGenerating) "Создаём видео..." else "Видео готово!",
                fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            if (isGenerating) "~30 секунд" else "Твоё лицо. Твой голос.",
            fontSize = 12.sp, color = TextSecondary,
        )
        Spacer(Modifier.height(14.dp))

        if (isGenerating) {
            GeneratingView(progress1 = progress1, progress2 = progress2, progress3 = progress3)
        } else {
            ReadyView(
                videoId = videoId,
                onTranslate = { navigationActions.navigateToTranslate(videoId) },
                onAddText = { navigationActions.navigateToTextPost() },
                onBack = { navigationActions.navigateToHome(popUpTo = true) },
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun GeneratingView(progress1: Float, progress2: Float, progress3: Float) {
    val infinite = rememberInfiniteTransition()
    val alpha by infinite.animateFloat(
        initialValue = 1f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
    )

    // Preview with shimmer
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PrimaryContainer)
            .border(1.5.dp, PrimaryContainerDark, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Surface),
                contentAlignment = Alignment.Center,
            ) { Text("👤", fontSize = 24.sp, modifier = Modifier.then(Modifier)) }

            // Sound wave animation
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(0.4f, 0.7f, 1f, 0.8f, 0.5f).forEachIndexed { i, h ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height((h * 16 * alpha).dp.coerceAtLeast(4.dp))
                            .clip(RoundedCornerShape(2.dp))
                            .background(Primary.copy(alpha = 0.6f + i * 0.05f)),
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(16.dp))

    ProgressRow("Создаём аватар", progress1, isDone = progress1 >= 1f)
    Spacer(Modifier.height(8.dp))
    ProgressRow("Синхронизируем речь", progress2, isDone = progress2 >= 1f)
    Spacer(Modifier.height(8.dp))
    ProgressRow("Переводим на English", progress3, isDone = progress3 >= 1f)
    Spacer(Modifier.height(12.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(PrimaryContainer)
            .border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("✨", fontSize = 11.sp)
                Text("ИИ говорит", fontSize = 9.sp, color = Primary)
            }
            Spacer(Modifier.height(4.dp))
            Text("Отличное фото — аватар будет реалистичным|", fontSize = 11.sp, color = Primary.copy(alpha = 0.85f), lineHeight = 16.sp)
        }
    }
}

@Composable
private fun ProgressRow(label: String, progress: Float, isDone: Boolean) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        ) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            if (isDone) {
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SecondaryContainer).padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("✓", fontSize = 8.sp, color = Secondary)
                    Text("Готово", fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
                }
            } else {
                Text("${(progress * 100).toInt()}%", fontSize = 9.sp, color = Primary)
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(5.dp)).background(BorderSecondary),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(5.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (isDone) Secondary else Primary),
            )
        }
    }
}

@Composable
private fun ReadyView(
    videoId: String,
    onTranslate: () -> Unit,
    onAddText: () -> Unit,
    onBack: () -> Unit,
) {
    // Video preview
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PrimaryContainer)
            .border(1.5.dp, Primary.copy(0.3f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(Surface),
            contentAlignment = Alignment.Center,
        ) { Text("▶", fontSize = 22.sp, color = Primary) }

        Row(modifier = Modifier.align(Alignment.TopStart).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            LangTag("RU", isPrimary = true)
            LangTag("EN", isPrimary = false)
        }
        Text("0:15", fontSize = 8.sp, color = TextSecondary,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).clip(RoundedCornerShape(4.dp)).background(Surface.copy(0.8f)).padding(horizontal = 4.dp, vertical = 2.dp))

        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp).fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).clip(RoundedCornerShape(2.dp)).background(Primary.copy(0.15f))) {
                Box(modifier = Modifier.fillMaxWidth(0.3f).height(2.dp).clip(RoundedCornerShape(2.dp)).background(Primary))
            }
        }
    }
    Spacer(Modifier.height(10.dp))

    // Stats
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

    // Actions
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        listOf("⬇", "↗", "↺").forEach { icon ->
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(10.dp)).padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) { Text(icon, fontSize = 16.sp) }
        }
    }
    Spacer(Modifier.height(12.dp))

    // Translate CTA
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(SecondaryContainer).border(0.5.dp, Secondary.copy(0.2f), RoundedCornerShape(14.dp)).padding(12.dp),
    ) {
        Column {
            Text("Перевести на English — 1 токен", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF085041))
            Text("1.5 млрд человек говорят на английском", fontSize = 9.sp, color = Secondary.copy(0.7f))
        }
    }
    Spacer(Modifier.height(8.dp))
    GreenButton("Перевести", onClick = onTranslate)
    Spacer(Modifier.height(6.dp))
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(14.dp)).clickable { onAddText() }.padding(12.dp),
        contentAlignment = Alignment.Center,
    ) { Text("Добавить текст для поста", fontSize = 12.sp, color = TextSecondary) }
}

@Composable
private fun LangTag(lang: String, isPrimary: Boolean) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(if (isPrimary) Primary else SecondaryContainer).padding(horizontal = 5.dp, vertical = 2.dp)
    ) { Text(lang, fontSize = 8.sp, color = if (isPrimary) Color.White else Secondary, fontWeight = FontWeight.Medium) }
}
