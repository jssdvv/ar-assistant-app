package com.jssdvv.ara.machines.domain.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.Measurement
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.dot
import io.github.sceneview.math.Position
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.atan2

private val translationFormat = DecimalFormat("0.######", DecimalFormatSymbols(Locale.US))
private val rotationFormat = DecimalFormat("0.##", DecimalFormatSymbols(Locale.US))

@Stable
class SingleTranslationState(
    initialUnits: String = "0",
    initialMeasurement: Measurement = Measurement.CENTIMETERS
) {
    private var _units by mutableStateOf(initialUnits)
    private var _measurement by mutableStateOf(initialMeasurement)

    val units: String
        get() = _units

    val measurement: Measurement
        get() = _measurement

    val numeric: Float
        get() = _units.toFloatOrNull() ?: 0f

    val meters: Float
        get() = ((_units.toDoubleOrNull() ?: 0.0) * _measurement.metersPerUnit).toFloat()

    val isNumeric: Boolean
        get() = _units.toDoubleOrNull() != null

    fun updateUnits(units: String) {
        _units = units
    }

    fun updateMeasurement(measurement: Measurement) {
        _measurement = measurement
    }
}

@Composable
fun rememberSingleTranslationState(
    initialMeters: Float = 0F,
    initialMeasurement: Measurement = Measurement.CENTIMETERS
): SingleTranslationState = remember {
    SingleTranslationState(
        translationFormat.format(initialMeters.toDouble() / initialMeasurement.metersPerUnit),
        initialMeasurement
    )
}

@Stable
class SingleRotationState(
    initialUnits: String = "0"
) {
    private var _units by mutableStateOf(initialUnits)

    val units: String
        get() = _units

    val degrees: Float
        get() = _units.toFloatOrNull() ?: 0f

    val isNumeric: Boolean
        get() = _units.toFloatOrNull() != null

    fun updateUnits(units: String) {
        _units = units
    }
}

@Composable
fun rememberSingleRotationState(
    initialDegrees: Float = 0F
): SingleRotationState = remember {
    SingleRotationState(
        rotationFormat.format(initialDegrees)
    )
}

@Stable
class MultiTranslationState(
    initialXUnits: String = "0",
    initialYUnits: String = "0",
    initialZUnits: String = "0",
    initialXMeasurement: Measurement = Measurement.CENTIMETERS,
    initialYMeasurement: Measurement = Measurement.CENTIMETERS,
    initialZMeasurement: Measurement = Measurement.CENTIMETERS,
) {
    val x = SingleTranslationState(initialXUnits, initialXMeasurement)
    val y = SingleTranslationState(initialYUnits, initialYMeasurement)
    val z = SingleTranslationState(initialZUnits, initialZMeasurement)

    val position: Position
        get() = Position(x.meters, y.meters, z.meters)
}

@Composable
fun rememberMultiTranslationState(
    initialXMeters: Float = 0F,
    initialYMeters: Float = 0F,
    initialZMeters: Float = 0F,
    initialXMeasurement: Measurement = Measurement.CENTIMETERS,
    initialYMeasurement: Measurement = Measurement.CENTIMETERS,
    initialZMeasurement: Measurement = Measurement.CENTIMETERS,
): MultiTranslationState = remember {
    MultiTranslationState(
        translationFormat.format(initialXMeters.toDouble() / initialXMeasurement.metersPerUnit),
        translationFormat.format(initialYMeters.toDouble() / initialYMeasurement.metersPerUnit),
        translationFormat.format(initialZMeters.toDouble() / initialZMeasurement.metersPerUnit),
        initialXMeasurement,
        initialYMeasurement,
        initialZMeasurement
    )
}

@Stable
class MultiRotationState(
    initialXUnits: String = "0",
    initialYUnits: String = "0",
    initialZUnits: String = "0",
) {
    val x = SingleRotationState(initialXUnits)
    val y = SingleRotationState(initialYUnits)
    val z = SingleRotationState(initialZUnits)

    val quaternion: Quaternion
        get() = Quaternion.fromEuler(Float3(x.degrees, y.degrees, z.degrees))
}

@Composable
fun rememberMultiRotationState(
    initialEulerDegrees: Float3 = Float3(),
): MultiRotationState = remember {
    MultiRotationState(
        rotationFormat.format(initialEulerDegrees.x),
        rotationFormat.format(initialEulerDegrees.y),
        rotationFormat.format(initialEulerDegrees.z)
    )
}

fun unidirectionalTransformPair(
    axis: Axis,
    translationUnits: Float,
    rotationUnits: Float,
): Pair<Position, Quaternion> = unidirectionalTranslation(axis, translationUnits) to
        unidirectionalRotation(axis, rotationUnits)

fun unidirectionalTranslation(axis: Axis, meters: Float) = axis.unitVector * meters
fun unidirectionalRotation(axis: Axis, degrees: Float) =
    Quaternion.fromEuler(axis.unitVector * degrees)

fun Quaternion.extractSingleAxisDegrees(axis: Axis): Float {
    val imaginaryQ = this.xyz
    val sinHalfTheta = dot(imaginaryQ, axis.unitVector)
    val radians = 2.0 * atan2(sinHalfTheta.toDouble(), this.w.toDouble())
    val degrees = Math.toDegrees(radians).toFloat()
    return degrees
}