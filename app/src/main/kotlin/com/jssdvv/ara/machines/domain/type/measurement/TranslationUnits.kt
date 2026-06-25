package com.jssdvv.ara.machines.domain.type.measurement

import androidx.annotation.StringRes
import com.jssdvv.ara.R

enum class TranslationUnits(
    @param:StringRes val symbolTextId: Int,
    val metersPerUnit: Float,
    val millisPerUnit: Int
) {
    METERS(
        symbolTextId = R.string.measurement_meters_symbol,
        metersPerUnit = 1F,
        millisPerUnit = 1000
    ),

    DECIMETERS(
        symbolTextId = R.string.measurement_decimeters_symbol,
        metersPerUnit = 0.1F,
        millisPerUnit = 100
    ),

    CENTIMETERS(
        symbolTextId = R.string.measurement_centimeters_symbol,
        metersPerUnit = 0.01F,
        millisPerUnit = 10
    ),

    MILLIMETERS(
        symbolTextId = R.string.measurement_millimeters_symbol,
        metersPerUnit = 0.001F,
        millisPerUnit = 1
    )
}