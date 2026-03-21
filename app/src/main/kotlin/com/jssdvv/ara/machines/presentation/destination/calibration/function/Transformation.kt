package com.jssdvv.ara.machines.presentation.destination.calibration.function

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R
import com.jssdvv.ara.machines.presentation.destination.calibration.function.RotationMode.DEGREES_01
import com.jssdvv.ara.machines.presentation.destination.calibration.function.RotationMode.DEGREES_10
import com.jssdvv.ara.machines.presentation.destination.calibration.function.RotationMode.DEGREES_45
import com.jssdvv.ara.machines.presentation.destination.calibration.function.RotationMode.HALF_DEGREE
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TranslationMode.CENTIMETERS
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TranslationMode.DECIMETERS
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TranslationMode.METERS
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TranslationMode.MILLIMETERS

enum class Transformation(
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
 * Enum representing the different modes of translation in the model calibration with a minimum
 * resolution of one (1) millimeter
 *
 * @property [METERS] A mode for translating in meters.
 * @property [DECIMETERS] A mode for translating in decimeters.
 * @property [CENTIMETERS] A mode for translating in centimeters.
 * @property [MILLIMETERS] A mode for translating in millimeters.
 */
enum class TranslationMode(
    @param:StringRes val symbolTextId: Int,
    val mmPerUnit: Int,
) {
    MILLIMETERS(
        symbolTextId = R.string.translation_mode_millimeters_symbol,
        mmPerUnit = 1
    ),
    CENTIMETERS(
        symbolTextId = R.string.translation_mode_centimeters_symbol,
        mmPerUnit = 10
    ),
    DECIMETERS(
        symbolTextId = R.string.translation_mode_decimeters_symbol,
        mmPerUnit = 100
    ),
    METERS(
        symbolTextId = R.string.translation_mode_meters_symbol,
        mmPerUnit = 1000
    )
}

/**
 * Enum representing the different modes of rotation in the model calibration with a minimum
 * resolution of half (0.5) a degree.
 *
 * @property [DEGREES_01] A mode for rotating in quaternions.
 * @property [DEGREES_10] A mode for rotating in quaternions.
 * @property [DEGREES_45] A mode for rotating in euler angles.
 * @property [HALF_DEGREE] A mode for rotating in euler angles.
 */
enum class RotationMode(
    @param:StringRes val symbolTextId: Int,
    val halfDegPerUnit: Int,
) {
    HALF_DEGREE(
        symbolTextId = R.string.rotation_mode_degrees_half_symbol,
        halfDegPerUnit = 1
    ),
    DEGREES_01(
        symbolTextId = R.string.rotation_mode_degrees_01_symbol,
        halfDegPerUnit = 2
    ),
    DEGREES_10(
        symbolTextId = R.string.rotation_mode_degrees_10_symbol,
        halfDegPerUnit = 20
    ),
    DEGREES_45(
        symbolTextId = R.string.rotation_mode_degrees_45_symbol,
        halfDegPerUnit = 90
    )
}

/**
 * Data class for the mode of transformation of a model in the editor.
 */
data class TransformationMode(
    val translation: TranslationMode = TranslationMode.DECIMETERS,
    val rotation: RotationMode = RotationMode.DEGREES_10,
)