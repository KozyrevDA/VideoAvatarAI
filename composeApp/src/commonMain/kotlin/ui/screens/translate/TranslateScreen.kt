package ui.screens.translate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import data.model.languages
import ui.navigation.AppNavigationActions
import ui.theme.Background
import ui.theme.BorderSecondary
import ui.theme.OnBackground
import ui.theme.Primary
import ui.theme.PrimaryContainer
import ui.theme.PrimaryContainerDark
import ui.theme.Secondary
import ui.theme.SecondaryContainer
import ui.theme.Surface
import ui.theme.TextSecondary
import ui.theme.TextTertiary
import ui.theme.Warning
import ui.theme.WarningContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslateScreen(
    navigationActions: AppNavigationActions,
    videoId: String,
) {
    var selectedLang by remember { mutableStateOf("English") }
    var useClonedVoice by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer).clickable { navigationActions.navigateBack() },
                    contentAlignment = Alignment.Center,
                ) { Text("←", fontSize = 16.sp, color = Primary) }
                Text("Перевести видео", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = OnBackground)
            }
            Spacer(Modifier.height(16.dp))

            // Video preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryContainer)
                    .border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text("▶", fontSize = 24.sp, color = Primary)
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                        .clip(RoundedCornerShape(5.dp)).background(Primary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) { Text("RU — исходное", fontSize = 8.sp, color = Color.White) }
            }
            Spacer(Modifier.height(16.dp))

            Text("На какой язык перевести?", fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                languages.forEach { lang ->
                    val sel = selectedLang == lang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (sel) PrimaryContainer else Surface)
                            .border(0.5.dp, if (sel) PrimaryContainerDark else BorderSecondary, RoundedCornerShape(20.dp))
                            .clickable { selectedLang = lang }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) { Text(if (sel) "$lang ✓" else lang, fontSize = 10.sp, color = if (sel) Primary else TextSecondary) }
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("Голос", fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(true to "Мой голос", false to "Стандартный").forEach { (mine, label) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (useClonedVoice == mine) PrimaryContainer else Surface)
                            .border(0.5.dp, if (useClonedVoice == mine) PrimaryContainerDark else BorderSecondary, RoundedCornerShape(20.dp))
                            .clickable { useClonedVoice = mine }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) { Text(if (useClonedVoice == mine) "$label ✓" else label, fontSize = 10.sp, color = if (useClonedVoice == mine) Primary else TextSecondary) }
                }
            }
            Spacer(Modifier.height(12.dp))

            // AI hint
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PrimaryContainer).border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(12.dp)).padding(10.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 12.sp)
                    Text(
                        when (selectedLang) {
                            "English" -> "English аудитория — 1.5 млрд человек"
                            "Español" -> "Spanish аудитория — 500 млн человек"
                            "中文" -> "Chinese аудитория — 1.1 млрд человек"
                            else -> "$selectedLang аудитория — миллионы новых зрителей"
                        },
                        fontSize = 11.sp,
                        color = Primary.copy(0.85f),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Токены", fontSize = 11.sp, color = TextSecondary)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(WarningContainer).padding(horizontal = 8.dp, vertical = 4.dp)
                ) { Text("27 осталось", fontSize = 9.sp, color = Warning, fontWeight = FontWeight.Medium) }
            }
            Spacer(Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Secondary)
                .clickable { navigationActions.navigateBack() }
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("Перевести — 1 токен", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}
