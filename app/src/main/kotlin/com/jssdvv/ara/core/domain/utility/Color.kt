package com.jssdvv.ara.core.domain.utility

import androidx.compose.ui.graphics.Color

fun Color.hue() : Float {
    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        hsv
    )
    return hsv[0]
}

fun Color.saturation() : Float {
    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        hsv
    )
    return hsv[1]
}