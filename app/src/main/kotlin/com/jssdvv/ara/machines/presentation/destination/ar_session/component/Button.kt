package com.jssdvv.ara.machines.presentation.destination.ar_session.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun LoopIconButton(
    isPaused: Boolean,
    isLooping: Boolean,
    onClick : (isLooping: Boolean) -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled : Boolean = true
) = Box(
    modifier = modifier
        .minimumInteractiveComponentSize()
        .clip(CircleShape)
        .combinedClickable(
            enabled = enabled,
            onClick = { onClick(isLooping) },
            onLongClick = onLongClick
        )
        .padding(12.dp),
    contentAlignment = Alignment.Center
) {
    CompositionLocalProvider(
        LocalContentColor provides if (enabled) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38F)
    ) {
        when {
            isPaused -> PauseIcon()
            isLooping -> LoopIcon()
            else -> PlayIcon()
        }
    }
}

@Composable
fun SkipNextOperationIconButton(
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    enabled : Boolean = true
) = IconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    content = { SkipNextOperationIcon() }
)

@Composable
fun SkipPreviousOperationIconButton(
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    enabled : Boolean = true
) = IconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    content = { SkipPreviousOperationIcon() }
)

@Composable
fun SkipNextStepIconButton(
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    enabled : Boolean = true
) = IconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    content = { SkipNextStepIcon() }
)

@Composable
fun SkipPreviousStepIconButton(
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    enabled : Boolean = true
) = IconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    content = { SkipPreviousStepIcon() }
)