package com.jssdvv.ara.machines.domain.model

import android.net.Uri
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

data class Marker(
    val id: Int = 0,
    val machineId: Int,
    val index: Int = 0,
    val imageUri: Uri = Uri.EMPTY,
    val sizeCentimeters: Float = 9F, // Marker Size
    val calibrated: Boolean = false,

    // Origin transform in the current Marker's local space.
    val originOffsetPosition: Position = Position(),
    val originOffsetRotation: Quaternion = Quaternion(),
)