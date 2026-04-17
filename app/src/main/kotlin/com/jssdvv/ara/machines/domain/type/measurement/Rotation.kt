package com.jssdvv.ara.machines.domain.type.measurement

import androidx.annotation.StringRes
import com.jssdvv.ara.R

enum class Rotation(
    @param:StringRes val symbolTextId: Int,
    val halfDegreesPerUnit: Int,
) {
    HALF_DEGREE(
        symbolTextId = R.string.rotation_mode_degrees_half_symbol,
        halfDegreesPerUnit = 1
    ),
    DEGREES_01(
        symbolTextId = R.string.rotation_mode_degrees_01_symbol,
        halfDegreesPerUnit = 2
    ),
    DEGREES_10(
        symbolTextId = R.string.rotation_mode_degrees_10_symbol,
        halfDegreesPerUnit = 20
    ),
    DEGREES_45(
        symbolTextId = R.string.rotation_mode_degrees_45_symbol,
        halfDegreesPerUnit = 90
    )
}