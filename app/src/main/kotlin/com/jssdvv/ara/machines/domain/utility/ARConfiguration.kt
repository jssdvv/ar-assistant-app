package com.jssdvv.ara.machines.domain.utility

import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session

fun configureARSession(
    session: Session,
    config: Config
) {
    config.apply {
        setFocusMode(Config.FocusMode.AUTO)
        setLightEstimationMode(Config.LightEstimationMode.DISABLED)
        setInstantPlacementMode(Config.InstantPlacementMode.DISABLED)
        setDepthMode(
            when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                true -> Config.DepthMode.AUTOMATIC
                else -> Config.DepthMode.DISABLED
            }
        )
    }

}

// From: https://developers.google.com/ar/develop/camera/flash/java
fun setTorch(session: Session, enabled: Boolean) {
    session.configure(
        session.config.apply {
            flashMode = if (enabled) Config.FlashMode.TORCH else Config.FlashMode.OFF
        }
    )
}

fun setSingleImageDatabase(session: Session, imageName: String, bitmap: Bitmap) {
    // Configures a single augmented image database each time
    // the selected marker changes. Avoiding problems like
    // markers being mixed up. Thank you google :)
    session.configure(
        session.config.setAugmentedImageDatabase(
            AugmentedImageDatabase(session).apply {
                addImage(imageName, bitmap)
            }
        )
    )
}

fun isTorchSupported(session: Session, context: Context): Boolean {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = session.cameraConfig.cameraId
    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
    return characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
}