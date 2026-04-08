package com.jssdvv.ara.machines.presentation.component

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.domain.utility.hue
import com.jssdvv.ara.core.domain.utility.saturation
import com.jssdvv.ara.core.presentation.theme.spacing
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

const val FLOAT_PI = PI.toFloat()

@Composable
fun OutlinedScrollWheel(
    color: Color,
    modifier: Modifier = Modifier,
    sides: Int = 16,
    onPressedChange: (Boolean) -> Unit = {},
    onDrag: (Int) -> Unit
) {
    Box(
        modifier = modifier
            .size(250.dp,40.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
    ) {
        ScrollWheel(
            sides = sides,
            modifier = Modifier.padding(MaterialTheme.spacing.small),
            color = color,
            onPressedChange = onPressedChange,
            onDrag = onDrag
        )
    }
}

@Composable
fun ScrollWheel(
    modifier: Modifier = Modifier,
    sides: Int = 16,
    @FloatRange(from = 0.0, to = 1.0) minLightness: Float = 0.1F,
    @FloatRange(from = 0.0, to = 1.0) maxLightness: Float = 0.7F,
    color: Color = Color(0xFF888888),
    onPressedChange: (Boolean) -> Unit = {},
    onDrag: (Int) -> Unit
) {
    // The stored rotation every angle step.
    var rotationRad by remember { mutableFloatStateOf(0F) }

    // If polygon is odd or even, n / 2 or 1 + n / 2.
    val visibleSides = sides / 2 + sides % 2

    val centralAngleRad = 2 * FLOAT_PI / sides // The angle step
    val halfCentralAngleRad = centralAngleRad / 2

    val hue = color.hue()
    val saturation = color.saturation()

    Canvas(
        modifier = modifier
            .size(250.dp, 40.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        onPressedChange(true)

                        // The radius of a circumscribed circle that passes
                        // through all the vertices of the polygon.
                        var fullRotationRad = 0F
                        var previousTicks = 0
                        val pointerId = down.id
                        val circumradius = size.width / 2F

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.find { it.id == pointerId }
                            if (change == null || !change.pressed) break

                            val dragAmount = change.positionChange().x
                            if (dragAmount != 0f) {

                                // The polygon rotation is similar every angle step
                                rotationRad = (rotationRad + dragAmount / circumradius) % centralAngleRad
                                fullRotationRad += dragAmount / circumradius

                                val ticks = (fullRotationRad / centralAngleRad).toInt()
                                if (ticks != previousTicks) {
                                    onDrag(ticks - previousTicks)
                                    previousTicks = ticks
                                }
                                change.consume()
                            }
                        }
                        onPressedChange(false)
                    }
                }
            }
    ) {
        for (side in 0 until visibleSides) {

            // The apothem angle starts at 0 radians on the bottom point of the vertical
            // line that crosses the center of the circumscribed circle of the polygon going
            // clockwise around the center
            val apothemAngleRad = side * centralAngleRad + rotationRad
            val faceAngleRad =
                if (apothemAngleRad > FLOAT_PI / 2) apothemAngleRad - FLOAT_PI else apothemAngleRad

            // The intensity is 1.0 when the face is in front at 0° and 0.0 at ±90°.
            val intensity = cos(faceAngleRad) // Used for face width and brightness

            val faceWidth = size.width * sin(halfCentralAngleRad) * intensity
            val faceLeft = size.width * (1 + sin(faceAngleRad - halfCentralAngleRad)) / 2

            // The range of lightness is from 0F (black)
            // and 1F (white) of the base color.
            val lightness =
                (intensity * (maxLightness - minLightness) + minLightness).coerceIn(0F, 1F)

            drawRect(
                color = Color.hsl(hue, saturation, lightness),
                topLeft = Offset(faceLeft, 0F),
                size = Size(faceWidth, size.height)
            )
        }
    }
}