package com.jssdvv.ara.machines.domain.type

import com.jssdvv.ara.R

enum class Measurement(
    val symbolTextId: Int,
    val metersPerUnit: Double
) {
    METERS(
        symbolTextId = R.string.measurement_meters_symbol,
        metersPerUnit = 1.0
    ),

    DECIMETERS(
        symbolTextId = R.string.measurement_decimeters_symbol,
        metersPerUnit = 0.1
    ),

    CENTIMETERS(
        symbolTextId = R.string.measurement_centimeters_symbol,
        metersPerUnit = 0.01
    ),

    MILLIMETERS(
        symbolTextId = R.string.measurement_millimeters_symbol,
        metersPerUnit = 0.001
    )
}