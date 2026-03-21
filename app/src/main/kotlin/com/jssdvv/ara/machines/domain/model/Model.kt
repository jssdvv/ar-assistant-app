package com.jssdvv.ara.machines.domain.model

import android.net.Uri
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

data class Model(
    val id: Int = 0,
    val machineId: Int,
    val name: String,
    val glbUri: Uri, // glTF Binary File
    val calibrated: Boolean = false,

    // Model's position and rotation in Origin's local space.
    val positionFromOrigin: Position = Position(0F,0F,0F),
    val rotationFromOrigin: Quaternion = Quaternion(),
)