package com.jssdvv.ara.scanner.presentation.destination.scanner.component

import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraPreview(
    modifier: Modifier,
    analyzer: ImageAnalysis.Analyzer,
    isTorchEnabled: Boolean,
    onPreviewSizeChanged: (width: Int, height: Int) -> Unit,
) {
    val localContext = LocalContext.current
    val localLifecycleOwner = LocalLifecycleOwner.current

    val executor = ContextCompat.getMainExecutor(localContext)
    val resolutionSelector = ResolutionSelector
        .Builder()
        .setResolutionStrategy(
            ResolutionStrategy(
                Size(1080, 720), // Scanner Resolution ~ 0.78 MP
                ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER
            )
        )
        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
        .build()

    val lifecycleCameraController = remember {
        LifecycleCameraController(localContext).apply {
            imageAnalysisResolutionSelector = resolutionSelector
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST

            isTapToFocusEnabled = true
            isPinchToZoomEnabled = false

            setImageAnalysisAnalyzer(executor, analyzer)
            bindToLifecycle(localLifecycleOwner)
        }
    }
    lifecycleCameraController.enableTorch(isTorchEnabled)

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            PreviewView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
                visibility = View.VISIBLE
                controller = lifecycleCameraController

                post { onPreviewSizeChanged(width, height) }
            }
        }
    )
}