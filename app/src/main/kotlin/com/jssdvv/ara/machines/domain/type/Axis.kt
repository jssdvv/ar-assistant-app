package com.jssdvv.ara.machines.domain.type

import androidx.annotation.StringRes
import com.jssdvv.ara.R
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_X_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_Y_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_Z_COLOR
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.abs

enum class Axis(
    @param:StringRes val rotationNameId: Int,
    val unitVector: Float3,
    val color: FloatArray,
    val quaternion: Quaternion
) {
    X(
        rotationNameId = R.string.enum_axis_x_name,
        unitVector = Float3(x = 1F),
        color = GIZMO_X_COLOR,
        quaternion = Quaternion(w = 0.707107F, x = 0.707107F)
    ),

    Y(
        rotationNameId = R.string.enum_axis_y_name,
        unitVector = Float3(y = 1F),
        color = GIZMO_Y_COLOR,
        quaternion = Quaternion(w = 0.707107F, y = 0.707107F)
    ),

    Z(
        rotationNameId = R.string.enum_axis_z_name,
        unitVector = Float3(z = 1F),
        color = GIZMO_Z_COLOR,
        quaternion = Quaternion(w = 0.707107F, z = 0.707107F)
    );

    companion object {
        fun fromVector(vector: Float3): Axis {
            val abs3 = abs(vector)
            return when {
                abs3.x >= abs3.y && abs3.x >= abs3.z -> X
                abs3.y >= abs3.x && abs3.y >= abs3.z -> Y
                else -> Z
            }
        }
    }
}