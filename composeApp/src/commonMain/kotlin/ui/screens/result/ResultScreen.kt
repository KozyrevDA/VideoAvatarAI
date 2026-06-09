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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import i18n.LocalStrings
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import platform.ShareResult
import platform.getVideoSharing
import ui.navigation.AppNavigationActions
import ui.theme.*

@Composable
fun ResultScreen(
    navigationActions: AppNavigationActions,
    videoId: String,
    viewModel: ResultViewModel = koinViewModel(parameters = {
    val s = LocalStrings.current parametersOf(videoId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val sharing = remember { getVideoSharing() }

    // Сообщения об успехе/ошибке операций
    var actionMessage by remember { mutableStateOf<String?>(null) }
    var isActionLoading by remember { mutableStateOf(false) }

    val infinite = rememberInfiniteTransition()
    val pulseAlpha by infinite.animateFloat(1f, 0.4f,
        infiniteRepeatable(tween(900), RepeatMode.Reverse))

    Column(
        modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()
            .padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))

        // Шапка
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer)
                .clickable { navigationActions.navigateBack() }, contentAlignment = Alignment.Center) {
                Text("←", fontSize = 16.sp, color = Primary)
            }
            Text(
                if (uiState.isGenerating) "Создаём видео..." else s.resultTitle,
                fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            if (uiState.isGenerating) "~30 секунд" else "Аватар говорит твоим голосом",
            fontSize = 12.sp, color = TextSecondary
        )
        Spacer(Modifier.height(14.dp))

        if (uiState.isGenerating) {
            GeneratingView(uiState, pulseAlpha)
        } else {
            ReadyView(
                videoUrl       = uiState.videoUrl,
                isActionLoading = isActionLoading,
                actionMessage  = actionMessage,
                onSave = {
                    val url = uiState.videoUrl ?: return@ReadyView
                    scope.launch {
                        isActionLoading = true
                        val result = sharing.saveToGallery(url, "neuroset_video_${System.currentTimeMillis()}")
                        actionMessage = when (result) {
                            is ShareResult.Success -> "✅ Сохранено в галерею"
                            is ShareResult.Error   -> "❌ ${result.message}"
                        }
                        isActionLoading = false
                    }
                },
                onShare = {
                    val url = uiState.videoUrl ?: return@ReadyView
                    scope.launch {
                        isActionLoading = true
                        sharing.shareVideo(url, "Смотри мой AI-аватар! 🤖🎬 #нейросеть #ИИ")
                        isActionLoading = false
                    }
                },
                onInstagram = {
                    val url = uiState.videoUrl ?: return@ReadyView
                    scope.launch {
                        isActionLoading = true
                        val result = sharing.shareToInstagramStories(url)
                        actionMessage = when (result) {
                            is ShareResult.Success -> null // Instagram открылся — сообщение не нужно
                            is ShareResult.Error   -> "❌ ${result.message}"
                        }
                        isActionLoading = false
                    }
                },
                onTikTok = {
                    val url = uiState.videoUrl ?: return@ReadyView
                    scope.launch {
                        isActionLoading = true
                        sharing.shareToTikTok(url)
                        isActionLoading = false
                    }
                },
                onTranslate = { navigationActions.navigateToTranslate(videoId) },
                onAddText    = { navigationActions.navigateToTextPost() },
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ── Вид: генерация идёт ──────────────────────────────────────────────────────

@Composable
private fun GeneratingView(uiState: ResultUiState, pulseAlpha: Float) {
    Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(20.dp))
        .background(PrimaryContainer).border(1.5.dp, PrimaryContainerDark, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Surface), contentAlignment = Alignment.Center) {
                Text("👤", fontSize = 24.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(0.4f,0.7f,1f,0.8f,0.5f).forEach { h ->
                    Box(modifier = Modifier.width(3.dp).height((h*16*pulseAlpha).dp.coerceAtLeast(4.dp))
                        .clip(RoundedCornerShape(2.dp)).background(Primary.copy(0.6f)))
                }
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    ProgressRow("Создаём аватар",        uiState.progress1)
    Spacer(Modifier.height(8.dp))
    ProgressRow("Синхронизируем речь",   uiState.progress2)
    Spacer(Modifier.height(8.dp))
    ProgressRow("Финальный рендер",      uiState.progress3)
}

@Composable
private fun ProgressRow(label: String, progress: Float) {
    val isDone = progress >= 1f
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            if (isDone) {
                Row(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SecondaryContainer).padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✓", fontSize = 8.sp, color = Secondary)
                    Text("Готово", fontSize = 8.sp, color = Secondary, fontWeight = FontWeight.Medium)
                }
            } else {
                Text("${(progress * 100).toInt()}%", fontSize = 9.sp, color = Primary)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(5.dp)).background(BorderSecondary)) {
            Box(modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(5.dp)
                .clip(RoundedCornerShape(5.dp)).background(if (isDone) Secondary else Primary))
        }
    }
}

// ── Вид: видео готово ────────────────────────────────────────────────────────

@Composable
private fun ReadyView(
    videoUrl: String?,
    isActionLoading: Boolean,
    actionMessage: String?,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onInstagram: () -> Unit,
    onTikTok: () -> Unit,
    onTranslate: () -> Unit,
    onAddText: () -> Unit,
) {
    // Превью видео
    Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(20.dp))
        .background(PrimaryContainer).border(1.5.dp, Primary.copy(0.3f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Surface), contentAlignment = Alignment.Center) {
            Text("▶", fontSize = 26.sp, color = Primary)
        }
        Row(modifier = Modifier.align(Alignment.TopStart).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            LangTag("RU", true); LangTag("EN", false)
        }
        Text("0:15", fontSize = 8.sp, color = TextSecondary,
            modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                .clip(RoundedCornerShape(4.dp)).background(Surface.copy(0.8f)).padding(horizontal = 4.dp, vertical = 2.dp))
    }
    Spacer(Modifier.height(12.dp))

    // Сообщение об операции
    actionMessage?.let {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(if (it.startsWith("✅")) SecondaryContainer else ErrorContainer)
            .padding(10.dp)) {
            Text(it, fontSize = 12.sp, color = if (it.startsWith("✅")) Secondary else Error)
        }
        Spacer(Modifier.height(8.dp))
    }

    // Кнопки действий
    if (isActionLoading) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
        }
        Spacer(Modifier.height(8.dp))
    }

    // Основные кнопки: Сохранить + Поделиться
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        // Сохранить в галерею
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = "⬇",
            label = "Сохранить",
            bg = Primary,
            textColor = Color.White,
            onClick = onSave,
        )
        // Поделиться (ShareSheet)
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = "↗",
            label = "Поделиться",
            bg = Surface,
            textColor = OnBackground,
            onClick = onShare,
        )
    }
    Spacer(Modifier.height(8.dp))

    // Кнопки соцсетей: Instagram + TikTok
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = "📸",
            label = "Instagram Stories",
            bg = Color(0xFFE1306C),
            textColor = Color.White,
            onClick = onInstagram,
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = "🎵",
            label = "TikTok",
            bg = Color(0xFF010101),
            textColor = Color.White,
            onClick = onTikTok,
        )
    }
    Spacer(Modifier.height(12.dp))

    // Статистика
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        listOf("40+" to "языков", "Твой" to "голос", "15 сек" to "видео").forEach { (v, l) ->
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Surface)
                .border(0.5.dp, BorderSecondary, RoundedCornerShape(14.dp)).padding(8.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(v, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                    Text(l, fontSize = 8.sp, color = TextTertiary)
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))

    // Перевести
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .background(SecondaryContainer).border(0.5.dp, Secondary.copy(0.2f), RoundedCornerShape(14.dp)).padding(12.dp)) {
        Column {
            Text("Перевести на English — 1 токен", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF085041))
            Text("1.5 млрд человек говорят на английском", fontSize = 9.sp, color = Secondary.copy(0.7f))
        }
    }
    Spacer(Modifier.height(8.dp))

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Secondary)
        .clickable { onTranslate() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
        Text("🌍 Перевести видео", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
    Spacer(Modifier.height(6.dp))

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface)
        .border(0.5.dp, BorderSecondary, RoundedCornerShape(14.dp)).clickable { onAddText() }.padding(12.dp),
        contentAlignment = Alignment.Center) {
        Text("✏️ Текст для поста", fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier,
    icon: String,
    label: String,
    bg: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(modifier = modifier.clip(RoundedCornerShape(14.dp)).background(bg)
        .border(if (bg == Color.White || bg.alpha < 0.1f) 0.dp else 0.dp, Color.Transparent, RoundedCornerShape(14.dp))
        .clickable { onClick() }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(icon, fontSize = 20.sp)
            Text(label, fontSize = 10.sp, color = textColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun LangTag(lang: String, isPrimary: Boolean) {
    Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(if (isPrimary) Primary else SecondaryContainer)
        .padding(horizontal = 5.dp, vertical = 2.dp)) {
        Text(lang, fontSize = 8.sp, color = if (isPrimary) Color.White else Secondary, fontWeight = FontWeight.Medium)
    }
}

private val ErrorContainer = Color(0xFFFFEBEE)
private val Error = Color(0xFFC62828)
