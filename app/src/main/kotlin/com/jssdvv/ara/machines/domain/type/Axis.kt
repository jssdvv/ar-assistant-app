package com.jssdvv.ara.machines.domain.type

import androidx.annotation.StringRes
import com.jssdvv.ara.R
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.abs

enum class Axis(
    @param:StringRes val rotationNameId: Int,
    val unitVector: Float3
) {
    X(
        rotationNameId = R.string.enum_axis_x_name,
        unitVector = Float3(x = 1F)
    ),

    Y(
        rotationNameId = R.string.enum_axis_y_name,
        unitVector = Float3(y = 1F)
    ),

    Z(
        rotationNameId = R.string.enum_axis_z_name,
        unitVector = Float3(z = 1F)
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