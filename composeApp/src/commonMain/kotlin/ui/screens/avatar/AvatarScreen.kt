package ui.screens.avatar

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
import i18n.LocalStrings
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.model.AvatarStyle
import org.koin.compose.viewmodel.koinViewModel
import ui.components.PrimaryButton
import ui.components.SectionTitle
import ui.navigation.AppNavigationActions
import ui.theme.*

@Composable
fun AvatarScreen(
    navigationActions: AppNavigationActions,
    viewModel: AvatarViewModel = koinViewModel(),
) {
    val s = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()
    val showImagePicker = remember { mutableStateOf(false) }
    val imagePicker = remember { getImagePicker() }

    // ImagePicker composable — ждёт showPicker=true чтобы открыть галерею
    imagePicker?.PickImageFromGallery(
        showPicker = showImagePicker,
        onPicked   = { bitmap ->
            val base64 = bitmap.toBase64()
            viewModel.onPhotoSelected(base64)
        },
    )

    Column(modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))

            // Шапка
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer)
                    .clickable { navigationActions.navigateBack() }, contentAlignment = Alignment.Center) {
                    Text("←", fontSize = 16.sp, color = Primary)
                }
                Text(s.avatarTitle, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            }
            Spacer(Modifier.height(18.dp))

            // Загрузка фото — теперь реально открывает галерею
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(20.dp))
                    .background(if (uiState.photoBase64 != null) SecondaryContainer else PrimaryContainer)
                    .border(1.5.dp, if (uiState.photoBase64 != null) Secondary else PrimaryContainerDark, RoundedCornerShape(20.dp))
                    .clickable {
                        imagePicker?.let { showImagePicker.value = true }
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.photoBase64 != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅", fontSize = 28.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(s.avatarPhotoSelected, fontSize = 12.sp, color = Secondary, fontWeight = FontWeight.Medium)
                        Text(s.avatarChangePhoto, fontSize = 10.sp, color = TextSecondary)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👤", fontSize = 28.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(s.avatarUploadPhoto, fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium)
                        Text(s.avatarUploadHint, fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            SectionTitle("ЧТО СКАЖЕТ АВАТАР")
            Spacer(Modifier.height(6.dp))
            TextField(
                value = uiState.text,
                onValueChange = { viewModel.onTextChange(it) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .border(0.5.dp, if (uiState.error != null) Error else BorderSecondary, RoundedCornerShape(14.dp)),
                placeholder = { Text(s.avatarTextPlaceholder, fontSize = 12.sp, color = TextTertiary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Surface, unfocusedContainerColor = Surface,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                ),
                minLines = 3, maxLines = 8,
            )
            if (uiState.error != null) {
                Spacer(Modifier.height(4.dp))
                Text(uiState.error!!, fontSize = 11.sp, color = Error)
            }
            Spacer(Modifier.height(16.dp))

            SectionTitle(s.avatarStyleLabel)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(Triple(AvatarStyle.BUSINESS, "💼", "Деловой"),
                       Triple(AvatarStyle.CASUAL, "😊", s.avatarStyleCasual),
                       Triple(AvatarStyle.EXPERT, "⭐", "Эксперт")).forEach { (style, emoji, label) ->
                    val sel = uiState.selectedStyle == style
                    Box(
                        modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(14.dp))
                            .background(if (sel) PrimaryContainer else Surface)
                            .border(if (sel) 1.5.dp else 0.5.dp, if (sel) Primary else BorderSecondary, RoundedCornerShape(14.dp))
                            .clickable { viewModel.onStyleSelect(style) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 16.sp)
                            Text(label, fontSize = 9.sp, color = if (sel) Primary else TextSecondary)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf("instagram" to "Instagram", "tiktok" to "TikTok", "vk" to "VK", "youtube" to "YouTube").forEach { (key, label) ->
                        val sel = uiState.selectedPlatform == key
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(if (sel) PrimaryContainer else Color.Transparent)
                                .border(0.5.dp, if (sel) PrimaryContainerDark else BorderSecondary, RoundedCornerShape(20.dp))
                                .clickable { viewModel.onPlatformSelect(key) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) { Text(if (sel) "$label ✓" else label, fontSize = 9.sp, color = if (sel) Primary else TextSecondary) }
                    }
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(WarningContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("${uiState.tokensCount} токенов", fontSize = 9.sp, color = Warning, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                PrimaryButton(s.avatarCreateBtn, onClick = {
                    viewModel.generate { taskId -> navigationActions.navigateToResult(taskId) }
                })
            }
        }
    }
}

private val Error = Color(0xFFC62828)
