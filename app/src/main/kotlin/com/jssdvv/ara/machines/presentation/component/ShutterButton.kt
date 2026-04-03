package com.jssdvv.ara.machines.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShutterButton(
    onClick: () -> Unit,
    onPress: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    colorEnabled: Color = Color.White,
    colorDisabled: Color = Color.DarkGray,
    ringSize: Dp = 72.dp,
    circleSize: Dp = 56.dp,
    strokeWidth: Dp = 5.dp,
    @DrawableRes iconDrawableId: Int? = null,
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val ringScale by animateFloatAsState(
        targetValue = if (isPressed) 1.4F else 1F,
        animationSpec = tween(200, 0, LinearOutSlowInEasing)
    )

    val circleScale by animateFloatAsState(
        targetValue = if (isPressed) 0.4F else 1F,
        animationSpec = tween(200, 0, LinearOutSlowInEasing)
    )
    Box(
        modifier = modifier
            .size(ringSize)
            .graphicsLayer { scaleX = ringScale; scaleY = ringScale }
            .clip(CircleShape)
            .pointerInput(enabled) {
                awaitPointerEventScope {
                    while (enabled) {
                        val down = awaitFirstDown()

                        val press = PressInteraction.Press(down.position)
                        interactionSource.tryEmit(press)

                        val upBeforeTimeout = withTimeoutOrNull(500) {
                            interactionSource.tryEmit(PressInteraction.Release(press))
                            waitForUpOrCancellation()
                        }

                        if (upBeforeTimeout == null) {
                            isPressed = true
                            onPress()
                            waitForUpOrCancellation()
                        } else {
                            onClick()
                        }

                        isPressed = false

                        interactionSource.tryEmit(PressInteraction.Release(press))
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    strokeWidth,
                    if (enabled) colorEnabled else colorDisabled,
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(circleSize)
                .graphicsLayer { scaleX = circleScale; scaleY = circleScale }
                .background(
                    if (enabled) colorEnabled else colorDisabled,
                    CircleShape
                )
                .clip(CircleShape)
                .indication(
                    interactionSource = interactionSource,
                    indication = ripple(color = Color.Black)
                )
        )
        iconDrawableId?.let { drawableId ->
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .graphicsLayer {
                        scaleX = circleScale
                        scaleY = circleScale
                        alpha = if (isPressed) 0f else 1f
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = drawableId),
                    contentDescription = null,
                    tint = if (enabled) Color.Black else Color.White.copy(alpha = .5F)
                )
            }
        }
    }
}

@Composable
fun ShutterSection(
    shutterEnabled: Boolean,
    onClickShutter: () -> Unit,
    onPressShutter: () -> Unit,
    modifier: Modifier = Modifier,
    leftModifier: Modifier = Modifier,
    rightModifier: Modifier = Modifier,
    rightSection: @Composable BoxScope.() -> Unit = {},
    leftSection: @Composable BoxScope.() -> Unit = {},
    containerColor: Color = Color.Black.copy(alpha = 0.4F),
    @DrawableRes iconDrawableId: Int? = null,
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 136.dp)
        .background(containerColor),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    Box(
        modifier = leftModifier.weight(1F),
        content = leftSection
    )
    ShutterButton(
        onClick = onClickShutter,
        onPress = onPressShutter,
        enabled = shutterEnabled,
        iconDrawableId = iconDrawableId
    )
    Box(
        modifier = rightModifier.weight(1F),
        content = rightSection
    )
}