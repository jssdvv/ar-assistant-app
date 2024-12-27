package com.jssdvv.ara.scanner.presentation.components

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawLandmark(landmark: PointF, color: Color, radius: Float) {
    drawCircle(
        color = color,
        radius = radius,
        center = Offset(landmark.x, landmark.y),
    )
}

fun DrawScope.drawBounds(topLeft: PointF, size: Size, color: Color, stroke: Float) {
    drawRect(
        color = color,
        size = size,
        topLeft = Offset(topLeft.x, topLeft.y),
        style = Stroke(width = stroke)
    )
}

fun DrawScope.drawTriangle(points: List<PointF>, color: Color, stroke: Float) {
    if (points.size < 3) return
    drawPath(
        path = Path().apply {
            moveTo(points[0].x, points[0].y)
            lineTo(points[1].x, points[1].y)
            lineTo(points[2].x, points[2].y)
            close()
        },
        color = color,
        style = Stroke(width = stroke)
    )
}



fun adjustPoint(point: PointF, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int): PointF {
    val x = point.x / imageWidth * screenWidth
    val y = point.y / imageHeight * screenHeight
    return PointF(x, y)
}

fun adjustSize(size: Size, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int): Size {
    val width = size.width / imageWidth * screenWidth
    val height = size.height / imageHeight * screenHeight
    return Size(width, height)
}