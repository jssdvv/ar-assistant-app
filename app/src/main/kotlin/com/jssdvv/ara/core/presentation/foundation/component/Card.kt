package com.jssdvv.ara.core.presentation.foundation.component

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jssdvv.ara.core.presentation.theme.cornerRadius
import com.jssdvv.ara.core.presentation.theme.spacing

@Composable
fun FocusableCard(
    onClick: () -> Unit,
    isFocused: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    focusBorderColor: Color = MaterialTheme.colorScheme.secondary,
    content: @Composable () -> Unit
) {
    val radius = MaterialTheme.cornerRadius.medium
    val separation = MaterialTheme.spacing.tiny
    val borderWidth = 1.dp
    val focusBorderWidth = 3.dp

    Surface(
        modifier = modifier
            .drawWithContent {
                drawContent()
                if (isFocused) {
                    val offset = (separation + focusBorderWidth / 2).toPx()
                    drawRoundRect(
                        color = focusBorderColor,
                        size = Size(size.width + 2 * offset, size.height + 2 * offset),
                        topLeft = Offset(-offset, -offset),
                        cornerRadius = CornerRadius(radius.toPx() + offset),
                        style = Stroke(focusBorderWidth.toPx())
                    )
                }
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(radius),
        border = BorderStroke(borderWidth, borderColor),
        color = color,
        contentColor = contentColor,
        content = content
    )
}

@Composable
fun HeroImageCard(
    onClick: () -> Unit,
    isFocused: Boolean,
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16 / 9F,
    contentScale: ContentScale = ContentScale.FillWidth,
    imageContentDescription: String? = null,
    content: @Composable () -> Unit
) = FocusableCard(
    onClick = onClick,
    isFocused = isFocused,
    modifier = modifier,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .build(),
            error = ColorPainter(Color.Gray),
            fallback = ColorPainter(Color.Gray),
            contentScale = contentScale,
            contentDescription = imageContentDescription
        )
        content()
    }
}