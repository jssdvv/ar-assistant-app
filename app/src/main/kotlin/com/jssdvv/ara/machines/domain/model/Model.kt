package com.jssdvv.ara.machines.domain.model

import android.net.Uri
import io.github.sceneview.math.Transform

data class Model(
    val id: Int = 0,
    val machineId: Int,
    val name: String,
    val glbUri: Uri, // glTF Binary File
    val calibrated: Boolean = false,

    // Model's position and rotation in Origin's local space.
    val offsetTransform: Transform  = Transform()
)