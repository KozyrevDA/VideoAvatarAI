package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ui.theme.BorderPrimary
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

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Primary)
            .clickable { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryContainer)
            .border(0.5.dp, PrimaryContainerDark, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Primary,
            fontSize = 13.sp,
        )
    }
}

@Composable
fun GreenButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Secondary)
            .clickable { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun PillTag(
    text: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
) {
    val bg = if (isPrimary) PrimaryContainer else SecondaryContainer
    val textColor = if (isPrimary) Primary else Secondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TagChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) PrimaryContainer else Color.Transparent)
            .border(
                if (selected) 0.5.dp else 0.5.dp,
                if (selected) PrimaryContainerDark else BorderSecondary,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Primary else TextSecondary,
            fontSize = 10.sp,
        )
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        color = TextTertiary,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.6.sp,
        modifier = modifier,
    )
}

@Composable
fun CardBase(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .border(0.5.dp, BorderSecondary, RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(12.dp),
    ) {
        content()
    }
}

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            val isDone = index < currentStep
            Box(
                modifier = Modifier
                    .size(if (isActive) 20.dp else 16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> Primary
                            isDone -> SecondaryContainer
                            else -> BorderSecondary
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isDone) "✓" else "${index + 1}",
                    color = when {
                        isActive -> Color.White
                        isDone -> Secondary
                        else -> TextTertiary
                    },
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            if (index < totalSteps - 1) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(0.5.dp)
                        .background(BorderSecondary)
                )
            }
        }
    }
}

@Composable
fun ProgressStepBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(BorderSecondary),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(currentStep.toFloat() / totalSteps)
                .height(3.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Primary),
        )
    }
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onHome: () -> Unit,
    onHistory: () -> Unit,
    onIdeas: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .border(0.5.dp, BorderPrimary, RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavItem("Главная", currentRoute == "home", onHome)
        NavItem("История", currentRoute == "history", onHistory)
        NavItem("Темы", currentRoute == "ideas", onIdeas)
        NavItem("Настройки", currentRoute == "settings", onSettings)
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = when (label) {
                "Главная" -> "⌂"
                "История" -> "◷"
                "Темы" -> "☆"
                else -> "⚙"
            },
            fontSize = 16.sp,
            color = if (selected) Primary else TextTertiary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 8.sp,
            color = if (selected) Primary else TextTertiary,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
        )
    }
}
