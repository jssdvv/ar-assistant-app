package com.jssdvv.ara.machines.domain.type.measurement

import androidx.annotation.StringRes
import com.jssdvv.ara.R

enum class TranslationUnits(
    @param:StringRes val symbolTextId: Int,
    val metersPerUnit: Double,
    val millisPerUnit: Int
) {
    METERS(
        symbolTextId = R.string.measurement_meters_symbol,
        metersPerUnit = 1.0,
        millisPerUnit = 1000
    ),

    DECIMETERS(
        symbolTextId = R.string.measurement_decimeters_symbol,
        metersPerUnit = 0.1,
        millisPerUnit = 100
    ),

    CENTIMETERS(
        symbolTextId = R.string.measurement_centimeters_symbol,
        metersPerUnit = 0.01,
        millisPerUnit = 10
    ),

    MILLIMETERS(
        symbolTextId = R.string.measurement_millimeters_symbol,
        metersPerUnit = 0.001,
        millisPerUnit = 1
    )
}