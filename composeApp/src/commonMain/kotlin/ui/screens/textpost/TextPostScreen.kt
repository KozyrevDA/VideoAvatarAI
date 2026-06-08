package ui.screens.textpost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.model.TextTone
import platform.getVideoSharing
import org.koin.compose.viewmodel.koinViewModel
import ui.components.PrimaryButton
import ui.components.SectionTitle
import ui.navigation.AppNavigationActions
import ui.theme.*

@Composable
fun TextPostScreen(
    navigationActions: AppNavigationActions,
    viewModel: TextPostViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val sharing = remember { getVideoSharing() }
    var copyMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer)
                    .clickable { navigationActions.navigateBack() }, contentAlignment = Alignment.Center) {
                    Text("←", fontSize = 16.sp, color = Primary)
                }
                Text("Текст для поста", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            }
            Spacer(Modifier.height(18.dp))

            Text("О чём пост?", fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            TextField(
                value = uiState.topic,
                onValueChange = { viewModel.onTopicChange(it) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(0.5.dp, BorderSecondary, RoundedCornerShape(12.dp)),
                placeholder = { Text("Паста карбонара — рецепт за 15 минут", fontSize = 12.sp, color = TextTertiary) },
                colors = TextFieldDefaults.colors(focusedContainerColor = Surface, unfocusedContainerColor = Surface, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                singleLine = true,
            )
            Spacer(Modifier.height(14.dp))

            SectionTitle("ПЛАТФОРМА")
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf("instagram" to "Instagram", "tiktok" to "TikTok", "vk" to "VK", "youtube" to "YouTube").forEach { (key, label) ->
                    val sel = uiState.platform == key
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (sel) PrimaryContainer else Color.Transparent).border(0.5.dp, if (sel) PrimaryContainerDark else BorderSecondary, RoundedCornerShape(20.dp)).clickable { viewModel.onPlatformSelect(key) }.padding(horizontal = 10.dp, vertical = 5.dp)) {
                        Text(if (sel) "$label ✓" else label, fontSize = 10.sp, color = if (sel) Primary else TextSecondary)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            SectionTitle("ТОН")
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(TextTone.FRIENDLY to "Дружелюбный", TextTone.EXPERT to "Экспертный",
                       TextTone.FUNNY to "С юмором", TextTone.MOTIVATIONAL to "Мотивация").forEach { (tone, label) ->
                    val sel = uiState.tone == tone
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (sel) PrimaryContainer else Color.Transparent).border(0.5.dp, if (sel) PrimaryContainerDark else BorderSecondary, RoundedCornerShape(20.dp)).clickable { viewModel.onToneSelect(tone) }.padding(horizontal = 10.dp, vertical = 5.dp)) {
                        Text(if (sel) "$label ✓" else label, fontSize = 10.sp, color = if (sel) Primary else TextSecondary)
                    }
                }
            }

            uiState.result?.let { result ->
                Spacer(Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderSecondary))
                Spacer(Modifier.height(12.dp))
                Text("Результат", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                Spacer(Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PrimaryContainer).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Column {
                        Text(result.text, fontSize = 12.sp, color = Primary.copy(0.9f), lineHeight = 18.sp)
                        if (result.hashtags.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Text(result.hashtags.joinToString(" ") { "#$it" }, fontSize = 11.sp, color = Primary.copy(0.6f))
                        }
                    }
                }

                // Сообщение о копировании
                copyMessage?.let { msg ->
                    Spacer(Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(SecondaryContainer).padding(8.dp)) {
                        Text(msg, fontSize = 11.sp, color = Secondary)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Изменить — очищает результат чтобы переписать тему
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(10.dp)).clickable { viewModel.clearResult() }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text("Изменить", fontSize = 10.sp, color = TextSecondary)
                    }
                    // Другой — генерирует новый вариант
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(10.dp)).clickable { viewModel.generate() }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text("Другой", fontSize = 10.sp, color = TextSecondary)
                    }
                    // Копировать — в буфер обмена
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(Primary).clickable {
                        val fullText = buildString {
                            append(result.text)
                            if (result.hashtags.isNotEmpty()) {
                                append("\n\n")
                                append(result.hashtags.joinToString(" ") { "#$it" })
                            }
                        }
                        sharing.copyToClipboard(fullText)
                        copyMessage = "✅ Скопировано!"
                    }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text("Копировать", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            } else {
                PrimaryButton(if (uiState.result != null) "Сгенерировать заново" else "Написать текст",
                    onClick = { copyMessage = null; viewModel.generate() })
            }
        }
    }
}
