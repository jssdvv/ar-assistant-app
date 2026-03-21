package com.jssdvv.ara.scanner.presentation.destination.scanner.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import com.google.mlkit.vision.barcode.common.Barcode
import kotlin.math.max

@Composable
fun QRSquareCanvas(
    barcode: Barcode,
    imageWidth: Int,
    imageHeight: Int,
    previewWidth: Int,
    previewHeight: Int,
    rotationDegrees: Int,
    onClick: (machineId: Int) -> Unit,
) {
    val localDensity = LocalDensity.current

    // Recalculates the width and height of the image after rotation
    // to match the preview width and height of the camera view.
    val (rotatedImageWidth, rotatedImageHeight) =
        if ((rotationDegrees / 90) % 2 != 0) {
            imageHeight to imageWidth
        } else {
            imageWidth to imageHeight
        }

    val scaleX = previewWidth.toFloat() / rotatedImageWidth
    val scaleY = previewHeight.toFloat() / rotatedImageHeight

    barcode.boundingBox?.let { rect ->

        // Bounding Box Rect
        val size = Size(rect.width() * scaleX, rect.height() * scaleY)
        val topLeft = Offset(rect.left * scaleX, rect.top * scaleY)
        val center = Offset(topLeft.x + (size.width / 2), topLeft.y + (size.height / 2))

        // Detected QR Rect
        val cornerRadius = CornerRadius(30F)
        val major = max(size.width, size.height)
        val fixedSizePx = Size(major, major) // Ensures a square with the major side
        val fixedTopLeftPx = Offset(center.x - (major / 2), center.y - (major / 2))

        val fixedSizeDp = with(localDensity) {
            DpSize(fixedSizePx.width.toDp(), fixedSizePx.height.toDp())
        }

        val fixedTopLeftDp = with(localDensity) {
            DpOffset(fixedTopLeftPx.x.toDp(), fixedTopLeftPx.y.toDp())
        }

        Box(
            modifier = Modifier
                .offset(fixedTopLeftDp.x, fixedTopLeftDp.y)
                .size(fixedSizeDp)
                .clickable {
                    barcode.rawValue?.let {
                        val regex = """M(\d+)""".toRegex()
                        regex.find(it)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let(onClick)
                    }
                }
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Fill
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset.Zero,
                    size = fixedSizePx,
                    cornerRadius = cornerRadius,
                    style = Fill,
                    alpha = 0.2f,
                )

                // Border
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset.Zero,
                    size = fixedSizePx,
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 10f),
                )
            }
        }
    }
}