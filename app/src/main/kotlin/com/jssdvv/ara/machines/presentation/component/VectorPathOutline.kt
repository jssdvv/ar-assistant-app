package com.jssdvv.ara.machines.presentation.component

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun VectorPathOutline(
    innerPath: Path,
    outerPath: Path,
    modifier: Modifier = Modifier,
    innerStrokeWidth: Float = 2F,
    outerStrokeWidth: Float = 4F,
    color: Color = Color.Black
) {
    val defaultMinWidth: Dp = 300.dp

    val combinedPaths = Path().apply {
        addPath(innerPath)
        addPath(outerPath)
    }

    val pathBounds = combinedPaths.getBounds()
    val aspectRatio = pathBounds.width / pathBounds.height

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .defaultMinSize(defaultMinWidth),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(minWidth, minHeight)) {
            val scaleX = (size.width - outerStrokeWidth) / pathBounds.width
            val scaleY = (size.height - outerStrokeWidth) / pathBounds.height
            val scale = min(scaleX, scaleY)

            val matrix = Matrix().apply {
                postTranslate(-pathBounds.left, -pathBounds.top)
                postScale(scale, scale)
                postTranslate(
                    (size.width - pathBounds.width * scale) / 2F,
                    (size.height - pathBounds.height * scale) / 2F
                )
            }

            val androidInnerPath = innerPath.asAndroidPath()
            val androidOuterPath = outerPath.asAndroidPath()

            val finalInnerPath = android.graphics.Path(androidInnerPath)
                .apply { transform(matrix) }
                .asComposePath()

            val finalOuterPath = android.graphics.Path(androidOuterPath)
                .apply { transform(matrix) }
                .asComposePath()

            drawPath(
                path = finalInnerPath,
                color = color,
                style = Stroke(
                    width = innerStrokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    miter = 0F
                )
            )

            drawPath(
                path = finalOuterPath,
                color = color,
                style = Stroke(
                    width = outerStrokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    miter = 0F
                )
            )
        }
    }
}