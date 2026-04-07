package com.jssdvv.ara.core.domain.utility

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

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

fun Color.asContentColor() : Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[1] = .2F
    hsv[2] = .9F
    return Color(android.graphics.Color.HSVToColor(hsv))
}

fun Color.asContainerColor() : Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[1] = .5F
    hsv[2] = .3F
    return Color(android.graphics.Color.HSVToColor(hsv))
}

fun Color.asBorderColor() : Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[1] = .4F
    hsv[2] = .6F
    return Color(android.graphics.Color.HSVToColor(hsv))
}