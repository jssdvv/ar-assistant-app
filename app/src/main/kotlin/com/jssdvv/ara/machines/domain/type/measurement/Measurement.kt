package com.jssdvv.ara.machines.domain.type.measurement

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R

enum class Measurement(
    @param:StringRes val labelTextId: Int,
    @param:DrawableRes val iconId: Int,
) {
    // These icons were extracted from the blender project
    // https://github.com/Shrinks99/blender-icons

    TRANSLATION(
        labelTextId = R.string.transformation_mode_translation_label,
        iconId = R.drawable.ic_translation,
    ),
    ROTATION(
        labelTextId = R.string.transformation_mode_rotation_label,
        iconId = R.drawable.ic_rotation,
    )
}

/**
 * Data class for the mode of transformation of a model in the editor.
 */
data class MeasurementMode(
    val translation: Translation = Translation.DECIMETERS,
    val rotation: Rotation = Rotation.DEGREES_10,
)