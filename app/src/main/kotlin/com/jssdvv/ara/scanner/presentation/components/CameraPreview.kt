package com.jssdvv.ara.scanner.presentation.components

import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun CameraPreview(
    modifier: Modifier,
    analyzer: ImageAnalysis.Analyzer,
    isTorchEnabled: Boolean,
) {
    val localContext = LocalContext.current
    val localLifecycleOwner = LocalLifecycleOwner.current

    val executor = ContextCompat.getMainExecutor(localContext)
    val lifecycleCameraController = remember {
        LifecycleCameraController(localContext).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            isTapToFocusEnabled = true
            isPinchToZoomEnabled = false
            enableTorch(false)
            setImageAnalysisAnalyzer(executor, analyzer)
            bindToLifecycle(localLifecycleOwner)
        }
    }
    lifecycleCameraController.enableTorch(isTorchEnabled)

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                controller = lifecycleCameraController
                lifecycleCameraController.bindToLifecycle(localLifecycleOwner)
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
                visibility = View.VISIBLE
            }
            previewView
        },
        modifier = modifier,
    )
}

@Composable
fun DrawBarcode(
    barcodes: List<Barcode>,
    imageWidth: Int,
    imageHeight: Int,
    screenWidth: Int,
    screenHeight: Int
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        barcodes.forEach { barcode ->
            barcode.boundingBox?.toComposeRect()?.let {
                val topLeft = adjustPoint(
                    PointF(it.topLeft.x, it.topLeft.y),
                    imageWidth,
                    imageHeight,
                    screenWidth,
                    screenHeight
                )
                val size = adjustSize(it.size, imageWidth, imageHeight, screenWidth, screenHeight)
                drawBounds(topLeft, size, Color.Yellow, 10f)
            }
        }
    }
}