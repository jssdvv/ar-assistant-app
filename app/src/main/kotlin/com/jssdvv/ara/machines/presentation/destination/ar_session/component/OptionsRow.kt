package com.jssdvv.ara.machines.presentation.destination.ar_session.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.foundation.component.TextIcon

enum class Speed(
    val denominator: Float,
    val displayText: String,
    @param:StringRes val notificationStringId: Int,
    @param:DrawableRes val notificationIconResId: Int
) {
    HALF(
        denominator = 0.5F,
        displayText = ".5×",
        notificationStringId = R.string.notification_chip_message_speed_half,
        notificationIconResId = R.drawable.ic_half_speed
    ),
    NORMAL(
        denominator = 1F,
        displayText = "1×",
        notificationStringId = R.string.notification_chip_message_speed_normal,
        notificationIconResId = R.drawable.ic_play
    ),
    DOUBLE(
        denominator = 2F,
        displayText = "2×",
        notificationStringId = R.string.notification_chip_message_speed_double,
        notificationIconResId = R.drawable.ic_double_speed
    ),
    QUADRUPLE(
        denominator = 4F,
        displayText = "4×",
        notificationStringId = R.string.notification_chip_message_speed_quadruple,
        notificationIconResId = R.drawable.ic_quadruple_speed
    )
}

@Composable
fun RowScope.OptionsRow(
    rowHeight: Dp,
    currentSpeed: Speed,
    isLooping: Boolean,
    isTorchEnabled: Boolean,
    onToggleTorch: () -> Unit,
    onToggleSpeed: () -> Unit,
    onToggleLoop: () -> Unit,
) {
    val iconSize = 20.dp
    val containerColor = Color.Black.copy(alpha = 0.3F)
    val contentColor = Color.White
    val colors = IconButtonDefaults.iconButtonColors(
        containerColor = containerColor,
        contentColor = contentColor
    )

    IconButton(
        onClick = onToggleTorch,
        modifier = Modifier.size(rowHeight),
        colors = if (isTorchEnabled) colors.copy(containerColor = Color(0xFFFDFA72).copy(alpha = 0.4F)) else colors,
        content = {
            Icon(
                painter = painterResource(if (isTorchEnabled) R.drawable.ic_torch_filled else R.drawable.ic_torch_outlined),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
        }
    )

    IconButton(
        onClick = onToggleSpeed,
        modifier = Modifier.size(rowHeight),
        colors = colors,
        content = {
            TextIcon(
                text = currentSpeed.displayText,
                size = 40.dp,
                containerColor = Color.Transparent,
                contentColor = contentColor,
                textStyle = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Black
            )
        }
    )

    IconButton(
        onClick = onToggleLoop,
        modifier = Modifier.size(rowHeight),
        colors = if (isLooping) colors.copy(containerColor = Color(0xFF00AAE4).copy(alpha = 0.4F)) else colors,
        content = {
            Icon(
                painter = painterResource(R.drawable.ic_loop),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
        }
    )
}