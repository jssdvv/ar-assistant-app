package com.jssdvv.ara.machines.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.machines.presentation.destination.ar_session.NotificationEvent
import kotlinx.coroutines.delay

@Composable
fun NotificationChip(
    event: NotificationEvent?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    duration: Long = 2000L,
    onDismiss: () -> Unit = {}
) {
    val expandDuration = 250
    val fadeDuration = 200
    val collapseDelay = 250L
    val appearDelay = 200L

    var currentPainter by remember { mutableStateOf<Int?>(null) }
    var currentText by remember { mutableStateOf<Int?>(null) }
    var currentArgs by remember { mutableStateOf<List<Any?>>(emptyList()) }

    var visible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val alphaState by animateFloatAsState(
        targetValue = if (visible) 1F else 0F,
        animationSpec = tween(200),
        label = "chip_alpha"
    )

    val offsetY by animateIntAsState(
        targetValue = if (visible) 0 else -24,
        animationSpec = tween(200),
        label = "chip_offset_y"
    )

    LaunchedEffect(event?.id) {
        if (event == null) return@LaunchedEffect
        if (event.message == null) return@LaunchedEffect

        if (visible) {
            expanded = false
            delay(collapseDelay)
        } else {
            visible = true
            delay(appearDelay)
        }

        currentPainter = event.iconRes
        currentText = event.message
        currentArgs = event.args

        expanded = true
        delay(duration)
        expanded = false
        delay(collapseDelay)
        visible = false
        onDismiss()
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = alphaState
                translationY = offsetY.toFloat()
            }
            .defaultMinSize(32.dp, 32.dp)
            .height(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(fadeDuration))
                    + expandHorizontally(tween(expandDuration), Alignment.CenterHorizontally),
            exit = fadeOut(tween(fadeDuration))
                    + shrinkHorizontally(tween(expandDuration), Alignment.CenterHorizontally)
        ) {
            currentText?.let { text ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .graphicsLayer {
                            alpha = if (expanded) 1F else 0F
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    currentPainter?.let { painter ->
                        Icon(
                            painter = painterResource(painter),
                            contentDescription = contentDescription,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Text(
                        text = stringResource(text, *currentArgs.filterNotNull().toTypedArray()),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}