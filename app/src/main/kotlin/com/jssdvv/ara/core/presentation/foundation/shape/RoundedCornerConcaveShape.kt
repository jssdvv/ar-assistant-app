package com.jssdvv.ara.core.presentation.foundation.shape

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

enum class ShapeEdge {
    Top,
    Right,
    Bottom,
    Left
}

abstract class TwoCornerBasedConcaveShape(
    val start: CornerSize,
    val end: CornerSize,
    val shapeEdge: ShapeEdge,
) : Shape {
    final override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        var start = start.toPx(size, density)
        var end = end.toPx(size, density)
        val width = size.width
        val height = size.height
        when (shapeEdge) {
            ShapeEdge.Top, ShapeEdge.Bottom -> {
                if (start + end > width) {
                    val scale = size.width / (start + end)
                    start *= scale
                    end *= scale
                }
            }

            ShapeEdge.Left, ShapeEdge.Right -> {
                if (start + end > height) {
                    val scale = size.height / (start + end)
                    start *= scale
                    end *= scale
                }
            }
        }
        require(start >= 0.0f && end >= 0.0f) {
            "Corner size in Px can't be negative(start = $start, end = $end)!"
        }
        return createOutline(
            size = size,
            start = start,
            end = end,
            shapeEdge = shapeEdge,
            layoutDirection = layoutDirection
        )
    }

    abstract fun createOutline(
        size: Size,
        start: Float,
        end: Float,
        shapeEdge: ShapeEdge,
        layoutDirection: LayoutDirection,
    ): Outline
}

class RoundedCornerConcaveShape(
    start: CornerSize,
    end: CornerSize,
    shapeEdge: ShapeEdge,
) : TwoCornerBasedConcaveShape(
    start = start,
    end = end,
    shapeEdge = shapeEdge,
) {
    override fun createOutline(
        size: Size,
        start: Float,
        end: Float,
        shapeEdge: ShapeEdge,
        layoutDirection: LayoutDirection,
    ): Outline = if (start + end == 0f) {
        Outline.Rectangle(size.toRect())
    } else {
        Outline.Generic(
            drawTwoCornerBasedConcaveShape(
                shapeSize = size,
                startRadius = start,
                endRadius = end,
                shapeEdge = shapeEdge
            )
        )
    }

    private fun drawTwoCornerBasedConcaveShape(
        shapeSize: Size,
        startRadius: Float,
        endRadius: Float,
        shapeEdge: ShapeEdge,
    ): Path {
        val startDiameter = 2 * startRadius
        val endDiameter = 2 * endRadius

        return Path().apply {
            when (shapeEdge) {
                ShapeEdge.Top -> {
                    reset()
                    lineTo(0f, -startRadius)
                    // Top left arc
                    arcTo(
                        rect = Rect(
                            left = 0f,
                            top = -startDiameter,
                            right = startDiameter,
                            bottom = 0f
                        ),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = -90f,
                        forceMoveTo = false
                    )
                    lineTo(x = shapeSize.width - endRadius, y = 0f)
                    // Top right arc
                    arcTo(
                        rect = Rect(
                            left = shapeSize.width - endDiameter,
                            top = -endDiameter,
                            right = shapeSize.width,
                            bottom = 0f
                        ),
                        startAngleDegrees = 90.0f,
                        sweepAngleDegrees = -90.0f,
                        forceMoveTo = false
                    )
                    lineTo(x = shapeSize.width, y = shapeSize.height)
                    lineTo(x = 0f, y = shapeSize.height)
                    lineTo(x = 0f, y = 0f)
                    close()
                }

                ShapeEdge.Right -> { TODO() }
                ShapeEdge.Bottom -> { TODO()}
                ShapeEdge.Left -> { TODO() }
            }
        }
    }
}

fun RoundedCornerConcaveShape(
    start: Float,
    end: Float,
    shapeEdge: ShapeEdge,
) = RoundedCornerConcaveShape(
    start = CornerSize(start),
    end = CornerSize(end),
    shapeEdge = shapeEdge
)

fun RoundedCornerConcaveShape(
    start: Dp,
    end: Dp,
    shapeEdge: ShapeEdge,
) = RoundedCornerConcaveShape(
    start = CornerSize(start),
    end = CornerSize(end),
    shapeEdge = shapeEdge
)

fun RoundedCornerConcaveShape(
    corners: Float,
    shapeEdge: ShapeEdge,
) = RoundedCornerConcaveShape(
    start = CornerSize(corners),
    end = CornerSize(corners),
    shapeEdge = shapeEdge
)

fun RoundedCornerConcaveShape(
    corners: Dp,
    shapeEdge: ShapeEdge,
) = RoundedCornerConcaveShape(
    start = CornerSize(corners),
    end = CornerSize(corners),
    shapeEdge = shapeEdge
)

fun RoundedCornerConcaveShape(
    corners: CornerSize,
    shapeEdge: ShapeEdge,
) = RoundedCornerConcaveShape(
    start = corners,
    end = corners,
    shapeEdge = shapeEdge
)