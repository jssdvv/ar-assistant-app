package com.jssdvv.ara.core.presentation.foundation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.theme.spacing

@Composable
fun BadgeIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    tint: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) = Box(
    modifier = modifier
        .size(48.dp)
        .background(containerColor, RoundedCornerShape(50)),
    contentAlignment = Alignment.Center
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun NumberedCircleIcon(
    number: Int,
    isFilled: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    thickness: Dp = 1.dp,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onPrimaryColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
) {
    val containerColor = if (isFilled) primaryColor else Color.Transparent
    val contentColor = if (isFilled) onPrimaryColor else primaryColor
    val borderStroke = if (!isFilled) BorderStroke(thickness, primaryColor) else null

    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
        border = borderStroke
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = textStyle,
                modifier = Modifier.padding(MaterialTheme.spacing.tiny)
            )
        }
    }
}