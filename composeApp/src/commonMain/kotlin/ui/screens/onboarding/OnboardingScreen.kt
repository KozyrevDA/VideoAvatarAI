package ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.ui.graphics.ImageBitmap
import ui.components.PrimaryButton
import ui.components.ProgressStepBar
import ui.components.SecondaryButton
import ui.components.StepIndicator
import ui.navigation.AppNavigationActions
import ui.theme.*

private val roles = listOf(
    Triple("Блогер", "Instagram, TikTok, YouTube", "👤"),
    Triple("Самозанятый", "Мастер, эксперт, фрилансер", "💼"),
    Triple("Малый бизнес", "Кафе, салон, магазин", "🏪"),
    Triple("SMM-специалист", "Веду чужие аккаунты", "📱"),
)

private val pains = listOf(
    Pair("Снять и сделать видео", "🎬"),
    Pair("Написать текст поста", "✏️"),
    Pair("Придумать темы для постов", "💡"),
    Pair("Перевести на другой язык", "🌍"),
    Pair("Публиковать вовремя", "⏰"),
)

@Composable
fun OnboardingScreen(
    navigationActions: AppNavigationActions,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    var step by remember { mutableStateOf(0) }
    var selectedRole by remember { mutableStateOf(-1) }
    val selectedPains = remember { mutableStateListOf<Int>() }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(visible = visible, enter = fadeIn()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(16.dp))
            when (step) {
                0 -> SplashStep(onStart = { step = 1 })
                1 -> RoleStep(
                    selectedRole = selectedRole,
                    onSelectRole = { selectedRole = it },
                    onNext = {
                        if (selectedRole >= 0) {
                            viewModel.saveQuizRole(roles[selectedRole].first)
                            step = 2
                        }
                    },
                )
                2 -> PainsStep(
                    selectedPains = selectedPains,
                    onTogglePain = { idx ->
                        if (selectedPains.contains(idx)) selectedPains.remove(idx)
                        else if (selectedPains.size < 2) selectedPains.add(idx)
                    },
                    onNext = {
                        viewModel.saveQuizPains(selectedPains.map { pains[it].first })
                        step = 3
                    },
                )
                3 -> PhotoStep(
                    onNext = { navigationActions.navigateToPaywall("onboarding") },
                    onSkip = { navigationActions.navigateToPaywall("onboarding") },
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SplashStep(onStart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 40.dp)) {
        Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(PrimaryContainer), contentAlignment = Alignment.Center) {
            Text("✨", fontSize = 28.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("Нейросеть Видео", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        Spacer(Modifier.height(6.dp))
        Text("ИИ создаёт контент для твоих соцсетей", fontSize = 13.sp, color = TextSecondary)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TagBadge("Видео из фото", isPurple = true)
            TagBadge("Текст поста", isGreen = true)
            TagBadge("Перевод", isOrange = true)
        }
        Spacer(Modifier.height(32.dp))
        PrimaryButton("Начать бесплатно", onClick = onStart)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("Apple", "Google", "VK").forEach { p ->
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).border(0.5.dp, BorderSecondary, RoundedCornerShape(10.dp)).background(Surface).clickable { onStart() }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text(p, fontSize = 11.sp, color = OnBackground)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Принимаешь условия использования", fontSize = 10.sp, color = TextTertiary)
    }
}

@Composable
private fun RoleStep(selectedRole: Int, onSelectRole: (Int) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepIndicator(0, 3, Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        ProgressStepBar(1, 3)
        Spacer(Modifier.height(16.dp))
        Text("Кто ты?", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        Spacer(Modifier.height(4.dp))
        Text("Настроим ИИ под тебя", fontSize = 12.sp, color = TextSecondary)
        Spacer(Modifier.height(14.dp))
        roles.forEachIndexed { idx, (name, sub, emoji) ->
            QuizOption(emoji, name, sub, selectedRole == idx) { onSelectRole(idx) }
            Spacer(Modifier.height(6.dp))
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Далее", onClick = onNext)
    }
}

@Composable
private fun PainsStep(selectedPains: List<Int>, onTogglePain: (Int) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepIndicator(1, 3, Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        ProgressStepBar(2, 3)
        Spacer(Modifier.height(16.dp))
        Text("Что отнимает время?", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        Spacer(Modifier.height(4.dp))
        Text("До 2 вариантов", fontSize = 12.sp, color = TextSecondary)
        Spacer(Modifier.height(14.dp))
        pains.forEachIndexed { idx, (pain, emoji) ->
            QuizOption(emoji, pain, "", selectedPains.contains(idx)) { onTogglePain(idx) }
            Spacer(Modifier.height(6.dp))
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Далее", onClick = onNext)
    }
}

@Composable
private fun PhotoStep(onNext: () -> Unit, onSkip: () -> Unit, showImagePicker: androidx.compose.runtime.MutableState<Boolean>, photoSelected: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepIndicator(2, 3, Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        ProgressStepBar(3, 3)
        Spacer(Modifier.height(16.dp))
        Text("Попробуй прямо сейчас", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnBackground)
        Spacer(Modifier.height(4.dp))
        Text("Создадим первое видео бесплатно", fontSize = 12.sp, color = TextSecondary)
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(18.dp)).background(if (photoSelected) SecondaryContainer else PrimaryContainer).border(1.5.dp, if (photoSelected) Secondary else PrimaryContainerDark, RoundedCornerShape(18.dp)).clickable { showImagePicker.value = true },
            contentAlignment = Alignment.Center,
        ) {
            if (photoSelected) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", fontSize = 32.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Фото загружено!", fontSize = 12.sp, color = Secondary, fontWeight = FontWeight.Medium)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👤", fontSize = 32.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Загрузи своё фото", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium)
                    Text("чёткий портрет", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Что скажет аватар?", fontSize = 11.sp, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(0.5.dp, BorderSecondary, RoundedCornerShape(12.dp)).background(Surface).padding(10.dp)) {
            Text("Привет! Сегодня покажу рецепт пасты за 15 минут...", fontSize = 11.sp, color = TextSecondary)
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Язык видео", fontSize = 11.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TagBadge("Русский ✓", isPurple = true)
                TagBadge("+ English")
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Создать видео", onClick = onNext)
        Spacer(Modifier.height(8.dp))
        SecondaryButton("Пропустить", onClick = onSkip)
    }
}

@Composable
private fun QuizOption(emoji: String, title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(if (selected) PrimaryContainer else Surface).border(if (selected) 1.5.dp else 0.5.dp, if (selected) Primary else BorderSecondary, RoundedCornerShape(14.dp)).clickable { onClick() }.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(if (selected) Primary else BorderSecondary), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 14.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, color = if (selected) Primary else OnBackground, fontWeight = FontWeight.Medium)
            if (subtitle.isNotEmpty()) Text(subtitle, fontSize = 10.sp, color = if (selected) Primary.copy(0.7f) else TextTertiary)
        }
        if (selected) {
            Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Primary), contentAlignment = Alignment.Center) {
                Text("✓", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun TagBadge(text: String, isPurple: Boolean = false, isGreen: Boolean = false, isOrange: Boolean = false) {
    val bg = when { isPurple -> PrimaryContainer; isGreen -> SecondaryContainer; isOrange -> Color(0xFFFEF3E2); else -> BorderSecondary }
    val color = when { isPurple -> Primary; isGreen -> Secondary; isOrange -> Color(0xFF92560A); else -> TextSecondary }
    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bg).padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(text, fontSize = 9.sp, color = color, fontWeight = FontWeight.Medium)
    }
}
