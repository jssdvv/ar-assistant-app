package com.jssdvv.ara.machines.presentation.destination.ar_session.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayerToolbar(
    isOperationPaused: Boolean,
    isOperationLooping: Boolean,
    modifier: Modifier = Modifier,
    onSkipPreviousStep: () -> Unit = {},
    onSkipPreviousOp: () -> Unit = {},
    onToggleLooping: (isLooping: Boolean) -> Unit = {},
    onPause: () -> Unit = {},
    onSkipNextOp: () -> Unit = {},
    onSkipNextStep: () -> Unit = {}
) {
    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier
    ) {
        SkipPreviousStepIconButton(
            onClick = onSkipPreviousStep
        )
        SkipPreviousOperationIconButton(
            onClick = onSkipPreviousOp
        )
        LoopIconButton(
            isPaused = isOperationPaused,
            isLooping = isOperationLooping,
            onClick = onToggleLooping,
            onLongClick = onPause
        )
        SkipNextOperationIconButton(
            onClick = onSkipNextOp
        )
        SkipNextStepIconButton(
            onClick = onSkipNextStep,
        )
    }
}

@Preview(showBackground = true, name = "Player Toolbar")
@Composable
fun PlayerToolbarPreview(
    modifier: Modifier = Modifier,
) {

    var currentStepIndex by remember { mutableStateOf(0) }
    val stepTotalSegments = 8

    var isOperationPaused by remember { mutableStateOf(false) }
    var isOperationLooping by remember { mutableStateOf(false) }
    var currentOperationIndex by remember { mutableStateOf(0) }
    val operationTotalSegments = 15

    val opAnimatableProgress = remember { Animatable(0F) }
    val updatedOperationLooping = rememberUpdatedState(isOperationLooping)

    val stepAnimatableProgress by remember {
        derivedStateOf {
            (currentOperationIndex + opAnimatableProgress.value) / operationTotalSegments.toFloat()
        }
    }

    LaunchedEffect(isOperationPaused) {
        if (isOperationPaused) {
            opAnimatableProgress.stop()
        } else {
            opAnimatableProgress.animateTo(
                targetValue = 1F,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
            opAnimatableProgress.snapTo(0f)

            when {
                currentOperationIndex < operationTotalSegments - 1 -> currentOperationIndex++
                else -> {
                    currentOperationIndex = 0
                    currentStepIndex = (currentStepIndex + 1).coerceAtMost(stepTotalSegments - 1)
                }
            }
        }
    }

    LaunchedEffect(currentOperationIndex, currentStepIndex) {
        do {
            opAnimatableProgress.snapTo(0f)
            opAnimatableProgress.animateTo(
                targetValue = 1F,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
        } while (updatedOperationLooping.value)

        opAnimatableProgress.snapTo(0f)

        when {
            currentOperationIndex < operationTotalSegments - 1 -> currentOperationIndex++
            else -> {
                currentOperationIndex = 0
                currentStepIndex = (currentStepIndex + 1).coerceAtMost(stepTotalSegments - 1)
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SegmentedProgressIndicator(
            totalSegments = stepTotalSegments,
            currentSegmentIndex = currentStepIndex,
            currentSegmentProgress = stepAnimatableProgress,
            modifier = Modifier.fillMaxWidth(),
            minSegmentWidth = MaterialTheme.spacing.extraLarge * 2,
            showNumbers = true,
            numbersOnTop = true
        )

        SegmentedProgressIndicator(
            totalSegments = operationTotalSegments,
            currentSegmentIndex = currentOperationIndex,
            currentSegmentProgress = opAnimatableProgress.value,
            modifier = Modifier.fillMaxWidth()
        )

        PlayerToolbar(
            isOperationPaused = isOperationPaused,
            isOperationLooping = isOperationLooping,
            modifier = Modifier.padding(16.dp),
            onSkipNextStep = {
                if (currentStepIndex < stepTotalSegments - 1) {
                    currentStepIndex++
                    currentOperationIndex = 0
                }
            },
            onSkipNextOp = {
                if (currentOperationIndex < operationTotalSegments - 1) {
                    currentOperationIndex++
                } else if (currentStepIndex < stepTotalSegments - 1) {

                    currentStepIndex++
                    currentOperationIndex = 0
                }
            },
            onToggleLooping = {
                if (isOperationPaused) {
                    isOperationPaused = false
                } else {
                    isOperationLooping = !isOperationLooping
                }
            },
            onPause = { isOperationPaused = true },
            onSkipPreviousOp = {
                if (currentOperationIndex > 0) {
                    currentOperationIndex--
                } else if (currentStepIndex > 0) {
                    currentStepIndex--
                    currentOperationIndex = operationTotalSegments - 1
                }
            },
            onSkipPreviousStep = {
                if (currentStepIndex > 0) {
                    currentStepIndex--
                    currentOperationIndex = 0
                }
            }
        )
    }
}