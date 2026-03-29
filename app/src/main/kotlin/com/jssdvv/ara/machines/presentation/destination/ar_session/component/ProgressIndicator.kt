package com.jssdvv.ara.machines.presentation.destination.ar_session.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.foundation.component.NumberedCircleIcon
import com.jssdvv.ara.core.presentation.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SegmentedProgressIndicator(
    totalSegments: Int,
    currentSegmentIndex: Int,
    currentSegmentProgress: Float,
    completedColor: Color = MaterialTheme.colorScheme.primary,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    pendingColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    minSegmentWidth: Dp = MaterialTheme.spacing.extraLarge,
    modifier: Modifier = Modifier,
    showNumbers: Boolean = true,
    numbersOnTop: Boolean = false,
) {
    val opListState = rememberLazyListState()

    LaunchedEffect(currentSegmentIndex) {
        val scrollIndex = (currentSegmentIndex - 1).coerceAtLeast(0)
        opListState.animateScrollToItem(scrollIndex)
    }

    BoxWithConstraints(modifier = modifier) {
        val segmentSpacing = MaterialTheme.spacing.small

        val segmentWidth = remember(maxWidth, totalSegments) {
            val totalSpacing = segmentSpacing * (totalSegments - 1)
            val availableWidth = maxWidth - totalSpacing
            val calculatedWidth = availableWidth / totalSegments
            calculatedWidth.coerceAtLeast(minSegmentWidth)
        }

        LazyRow(
            state = opListState,
            horizontalArrangement = Arrangement.spacedBy(segmentSpacing)
        ) {
            items(totalSegments) { index ->
                val (segmentProgress, indicatorColor) = when {
                    index < currentSegmentIndex -> 1F to completedColor
                    index == currentSegmentIndex -> currentSegmentProgress to activeColor
                    else -> 0F to pendingColor
                }

                val indicator = @Composable {
                    LinearWavyProgressIndicator(
                        progress = { segmentProgress },
                        modifier = Modifier.width(segmentWidth),
                        color = indicatorColor,
                        trackColor = pendingColor,
                        gapSize = 0.dp,
                        stopSize = 0.dp,
                        amplitude = { if (index == currentSegmentIndex) 0.5F else 0F },
                        wavelength = 10.dp
                    )
                }

                val number = @Composable {
                    if (showNumbers) {
                        NumberedCircleIcon(
                            number = index + 1,
                            isFilled = index <= currentSegmentIndex,
                            primaryColor = indicatorColor
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (numbersOnTop) {
                        number()
                        indicator()
                    } else {
                        indicator()
                        number()
                    }
                }
            }
        }
    }
}