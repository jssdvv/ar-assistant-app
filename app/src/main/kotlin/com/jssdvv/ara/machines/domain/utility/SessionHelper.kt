package com.jssdvv.ara.machines.domain.utility

import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.google.android.filament.Skybox
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import io.github.sceneview.environment.Environment
import io.github.sceneview.loaders.EnvironmentLoader

fun configureARSession(
    session: Session,
    config: Config
) {
    config.apply {
        setFocusMode(Config.FocusMode.AUTO)
        setLightEstimationMode(Config.LightEstimationMode.DISABLED)
        setInstantPlacementMode(Config.InstantPlacementMode.DISABLED)
        planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
        setDepthMode(
            when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                true -> Config.DepthMode.AUTOMATIC
                else -> Config.DepthMode.DISABLED
            }
        )
    }

}

fun isTorchSupported(session: Session, context: Context): Boolean {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = session.cameraConfig.cameraId
    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
    return characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
}

// From: https://developers.google.com/ar/develop/camera/flash/java
fun Session.setTorch(enabled: Boolean) {
    configure(
        config.apply { flashMode = if (enabled) Config.FlashMode.TORCH else Config.FlashMode.OFF }
    )
}

fun Session.setImageDatabase(imageName: String, bitmap: Bitmap) {
    // Configures a single augmented image database each time
    // the selected marker changes. Avoiding problems like
    // markers being mixed up. Thank you google :)
    configure(
        config.setAugmentedImageDatabase(
            AugmentedImageDatabase(this).apply { addImage(imageName, bitmap) }
        )
    )
}

fun EnvironmentLoader.createMainEnvironment() : Environment {
    return createEnvironment(
        skybox = Skybox
            .Builder()
            .color(0.2F, 0.2F, 0.2F, 1.0F)
            .build(engine)
    )
}