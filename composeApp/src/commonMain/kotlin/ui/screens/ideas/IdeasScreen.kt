package ui.screens.ideas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import platform.getVideoSharing
import org.koin.compose.viewmodel.koinViewModel
import ui.navigation.AppNavigationActions
import ui.screens.home.BottomNav
import ui.theme.*

@Composable
fun IdeasScreen(
    navigationActions: AppNavigationActions,
    viewModel: IdeasViewModel = koinViewModel(),
) {
    val s = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()
    val sharing = remember { getVideoSharing() }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Background).statusBarsPadding()) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer).clickable { navigationActions.navigateBack() }, contentAlignment = Alignment.Center) {
                        Text("←", fontSize = 16.sp, color = Primary)
                    }
                    Text(s.ideasTitle, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground)
                }
                if (uiState.ideas.isNotEmpty()) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(PrimaryContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("${uiState.ideas.size} идей", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            Text(s.ideasNicheLabel, fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            TextField(
                value = uiState.niche,
                onValueChange = { viewModel.onNicheChange(it) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(0.5.dp, BorderSecondary, RoundedCornerShape(12.dp)),
                placeholder = { Text(s.ideasNichePlaceholder, fontSize = 12.sp, color = TextTertiary) },
                colors = TextFieldDefaults.colors(focusedContainerColor = Surface, unfocusedContainerColor = Surface, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                singleLine = true,
            )

            // Toast message
            toastMessage?.let {
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(SecondaryContainer).padding(8.dp)) {
                    Text(it, fontSize = 11.sp, color = Secondary)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (uiState.ideas.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                itemsIndexed(uiState.ideas) { idx, idea ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable {
                                // Нажатие на идею — копируем в буфер
                                sharing.copyToClipboard(idea)
                                toastMessage = "✅ Скопировано: ${idea.take(40)}..."
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Primary, modifier = Modifier.padding(top = 1.dp))
                        Text(idea, fontSize = 12.sp, color = OnBackground, lineHeight = 18.sp, modifier = Modifier.weight(1f))
                        Text("📋", fontSize = 14.sp)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderSecondary))
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        } else {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(s.ideasEmpty, fontSize = 13.sp, color = TextTertiary, textAlign = TextAlign.Center)
            }
        }

        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (uiState.ideas.isNotEmpty()) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Surface).border(0.5.dp, BorderSecondary, RoundedCornerShape(14.dp)).clickable { viewModel.generate() }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text(s.ideasRefreshBtn, fontSize = 12.sp, color = TextSecondary)
                }
                // Копировать все идеи
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Primary).clickable {
                    val all = uiState.ideas.mapIndexed { i, idea -> "${i+1}. $idea" }.joinToString("\n")
                    sharing.copyToClipboard(all)
                    toastMessage = "✅ ${s.ideasAllCopied}"
                }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text(s.ideasCopyAllBtn, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                }
            } else {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Primary).clickable { viewModel.generate() }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text(s.ideasGenerateBtn, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
