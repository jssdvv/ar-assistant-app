package com.jssdvv.ara.machines.presentation.sceneview.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.measurement.TranslationUnits
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


typealias NumericString = String

private val numericRegex = Regex("^-?\\d*(\\.\\d*)?$")
private val leadingZerosRegex = Regex("^(-?)0+(?=[0-9])")

private fun NumericString.update(new: String): String {
    if (new.isEmpty()) return new
    if (!numericRegex.matches(new)) return this
    return new.replace(leadingZerosRegex, "$1")
}

data class NumericString3(
    val x: NumericString = "0",
    val y: NumericString = "0",
    val z: NumericString = "0"
) {
    constructor(v: NumericString) : this(x = v, y = v, z = v)

    fun updateX(new: NumericString) = copy(x = x.update(new))
    fun updateY(new: NumericString) = copy(y = y.update(new))
    fun updateZ(new: NumericString) = copy(z = z.update(new))

    fun update(new: NumericString3) = NumericString3(
        x = x.update(new.x),
        y = y.update(new.y),
        z = z.update(new.z)
    )

    fun toFloat3() = Float3(
        x = x.toFloat(),
        y = y.toFloat(),
        z = z.toFloat()
    )
}

fun NumericString.toFloat() = this.toFloatOrNull() ?: 0F

fun Float.toNumericString() = this.toString()

fun Float3.toNumericString3() = NumericString3(
    x = x.toNumericString(),
    y = y.toNumericString(),
    z = z.toNumericString()
)

private val translationFormat = DecimalFormat("0.######", DecimalFormatSymbols(Locale.US))
private val rotationFormat = DecimalFormat("0.##", DecimalFormatSymbols(Locale.US))
private val timeFormat = DecimalFormat("0.##", DecimalFormatSymbols(Locale.US))

@Stable
class TimeState(initialSeconds: NumericString = "0") {
    var value by mutableStateOf(initialSeconds)
        private set

    val seconds: Float
        get() = value.toFloatOrNull() ?: 0F

    fun updateValue(value: String) {
        this.value = this.value.update(value)
    }
}

@Composable
fun rememberTimeState(initialDegrees: Float = 0F): TimeState = remember {
    TimeState(timeFormat.format(initialDegrees))
}

@Stable
class SingleTranslationState(
    initialValue: NumericString = "0",
    initialUnits: TranslationUnits = TranslationUnits.CENTIMETERS
) {
    var value: NumericString by mutableStateOf(initialValue)
        private set

    var units: TranslationUnits by mutableStateOf(initialUnits)

    val meters: Float
        get() = value.toFloat() * units.metersPerUnit

    fun updateMeters(meters: Float) {
        value = (meters * units.metersPerUnit).toNumericString()
    }

    fun updateMeters(meters: Float, units: TranslationUnits) {
        value = (meters * units.metersPerUnit).toNumericString()
    }

    fun updateValue(value: NumericString) {
        this.value = value.update(value)
    }
}

@Stable
class PitchTurnsState(
    initialValue: NumericString = "0",
    useTurns: Boolean = true,
    initialUnits: TranslationUnits = TranslationUnits.MILLIMETERS
) {
    var value: NumericString by mutableStateOf(initialValue)
        private set

    var units: TranslationUnits by mutableStateOf(initialUnits)

    var useTurns: Boolean by mutableStateOf(useTurns)

    private val numeric: Float
        get() = value.toFloatOrNull() ?: 0F

    private val meters: Float
        get() = numeric * units.metersPerUnit

    fun pitch(metersDistance: Float): Float = when {
        !useTurns -> meters
        metersDistance != 0F && numeric != 0F -> metersDistance / numeric
        else -> 0F
    }

    fun turns(metersDistance: Float): Float = when {
        useTurns -> numeric
        meters > metersDistance -> 1F
        metersDistance != 0F && meters != 0F -> metersDistance / meters
        else -> 0F
    }

    fun updateValue(value: NumericString) {
        this.value = this.value.update(value)
    }

    fun toggleTurns() {
        useTurns = !useTurns
    }

    fun updateUseTurns(value: Boolean) {
        useTurns = value
    }
}

@Stable
class TranslationState(
    initialValue: NumericString3 = NumericString3(),
    initialUnits: TranslationUnits = TranslationUnits.CENTIMETERS
) {
    val x = SingleTranslationState(initialValue.x, initialUnits)
    val y = SingleTranslationState(initialValue.y, initialUnits)
    val z = SingleTranslationState(initialValue.z, initialUnits)

    val value: NumericString3
        get() = NumericString3(x.value, y.value, z.value)

    val meters: Float3
        get() = Float3(x.meters, y.meters, z.meters)

    fun updateMeters(meters: Float3) {
        x.updateMeters(meters.x)
        y.updateMeters(meters.y)
        z.updateMeters(meters.z)
    }

    fun updateMeters(meters: Float, universalUnits: TranslationUnits) {
        x.updateMeters(meters, universalUnits)
        y.updateMeters(meters, universalUnits)
        z.updateMeters(meters, universalUnits)
    }

    fun updateValue(value: NumericString3) {
        x.updateValue(value.x)
        y.updateValue(value.y)
        z.updateValue(value.z)
    }
}

@Stable
class SingleOrientationState(
    initialValue: NumericString = "0",
) {
    var value: NumericString by mutableStateOf(initialValue)
        private set

    val degrees: Float
        get() = value.toFloat()

    fun updateDegrees(degrees: Float) {
        value = degrees.toNumericString()
    }

    fun updateValue(value: NumericString) {
        this.value = value.update(value)
    }
}

@Stable
class OrientationState(
    initialValue: NumericString3 = NumericString3()
) {
    val x = SingleOrientationState(initialValue.x)
    val y = SingleOrientationState(initialValue.y)
    val z = SingleOrientationState(initialValue.z)

    val value: NumericString3
        get() = NumericString3(x.value, y.value, z.value)

    val degrees: Float3
        get() = Float3(x.degrees, y.degrees, z.degrees)

    fun updateDegrees(degrees: Float3) {
        x.updateDegrees(degrees.x)
        y.updateDegrees(degrees.y)
        z.updateDegrees(degrees.z)
    }

    fun updateValue(value: NumericString3) {
        x.updateValue(value.x)
        y.updateValue(value.y)
        z.updateValue(value.z)
    }
}

@Stable
class TransformState(
    initialTranslation: NumericString3 = NumericString3(), // Offset Vector
    initialUniversalTranslation: NumericString = "0",
    initialOrientation: NumericString3 = NumericString3(), // Euler angles
    initialUniversalOrientation: NumericString = "0",
    initialUnits: TranslationUnits = TranslationUnits.CENTIMETERS,
    initialAxis: Axis = Axis.Y,
    initialTurns: NumericString = "2"
) {
    val multiTranslation = TranslationState(initialTranslation, initialUnits)

    val multiOrientation = OrientationState(initialOrientation)

    var universalAxis: Axis by mutableStateOf(initialAxis)

    val universalTranslation = SingleTranslationState(
        initialValue = initialUniversalTranslation,
        initialUnits = initialUnits
    )

    val universalOrientation = SingleOrientationState(
        initialValue = initialUniversalOrientation,
    )

    val universalPitchTurns = PitchTurnsState(
        initialValue = initialTurns
    )

    val turns: Float
        get() = universalPitchTurns.turns(universalTranslation.meters)

    val singleAxisTranslationMeters: Float3
        get() = universalAxis.unitVector * universalTranslation.meters

    val singleAxisQuaternionDegrees: Quaternion
        get() = Quaternion.fromAxisAngle(universalAxis.unitVector, universalOrientation.degrees)
}

@Composable
fun rememberTransformState(
    initialPosition: Position = Position(),
    initialQuaternion: Quaternion = Quaternion(),
    initialUnits: TranslationUnits = TranslationUnits.CENTIMETERS,
    initialAxis: Axis = Axis.Y,
    initialTurns: Float = 2F
): TransformState {
    val translation = NumericString3(
        x = translationFormat.format(initialPosition.x.toDouble() / initialUnits.metersPerUnit),
        y = translationFormat.format(initialPosition.y.toDouble() / initialUnits.metersPerUnit),
        z = translationFormat.format(initialPosition.z.toDouble() / initialUnits.metersPerUnit),
    )

    return remember {
        TransformState(
            initialTranslation = translation,
            initialUniversalTranslation = when (initialAxis) {
                Axis.X -> translation.x
                Axis.Y -> translation.y
                Axis.Z -> translation.z
            },
            initialOrientation = initialQuaternion.toEulerAngles().toNumericString3(),
            initialUniversalOrientation = initialQuaternion
                .degreesFromAxis(initialAxis)
                .toNumericString(),
            initialUnits = initialUnits,
            initialAxis = initialAxis,
            initialTurns = translationFormat.format(initialTurns.toDouble())
        )
    }
}