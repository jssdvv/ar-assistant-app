package com.jssdvv.ara.core.presentation.foundation.component

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jssdvv.ara.core.presentation.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.extraLarge,
            color = color,
            contentColor = contentColor,
        ) {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.large),
                content = { content() }
            )
        }
    }
}

@Composable
fun DialogHeroImage(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16 / 9F,
    imageUri: Uri?,
    contentScale: ContentScale = ContentScale.FillWidth,
    imageContentDescription: String? = null,
    content: @Composable () -> Unit
) = MinimalDialog(
    onDismissRequest = onDismissRequest,
    modifier = modifier
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