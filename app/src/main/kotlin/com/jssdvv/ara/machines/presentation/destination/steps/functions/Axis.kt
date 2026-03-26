package com.jssdvv.ara.machines.presentation.destination.steps.functions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.machines.presentation.destination.steps.component.SquareButton
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

enum class Axis(
    val rotationName: String,
    val unitVector: Float3
) {
    X(
        rotationName = "Roll",
        unitVector = Float3(x = 1F)
    ), // todo create strings

    Y(
        rotationName = "Pitch",
        unitVector = Float3(y = 1F)
    ),

    Z(
        rotationName = "Yaw",
        unitVector = Float3(z = 1F)
    ),
}

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

@Composable
fun AxisSelector(
    axis: Axis,
    onAxisChange: (Axis) -> Unit,
    modifier: Modifier = Modifier,
    isRotation: Boolean = false
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 3,
        maxLines = 3
    ) {
        Axis.entries.forEach {
            SquareButton(
                selected = it == axis,
                onClick = { onAxisChange(it) },
                text = if (isRotation) it.rotationName else it.name
            )
        }
    }
}

@Stable
class TranslationState(
    initialUnits: String = "0",
    initialMeasurement: Measurement = Measurement.CENTIMETERS
) {
    private var _units by mutableStateOf(initialUnits)
    private var _measurement by mutableStateOf(initialMeasurement)

    val units: String
        get() = _units

    val measurement: Measurement
        get() = _measurement

    val numeric : Float
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
fun rememberTranslationState(
    initialUnits: String = "0",
    initialMeasurement: Measurement = Measurement.CENTIMETERS
): TranslationState = remember { TranslationState(initialUnits, initialMeasurement) }

@Stable
class RotationState(
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
fun rememberRotationState(
    units: String = "0"
): RotationState = remember { RotationState(units) }

@Stable
class MultiAxisTranslationState(
    initialXUnits: String = "0",
    initialYUnits: String = "0",
    initialZUnits: String = "0",
    initialXMeasurement: Measurement = Measurement.CENTIMETERS,
    initialYMeasurement: Measurement = Measurement.CENTIMETERS,
    initialZMeasurement: Measurement = Measurement.CENTIMETERS,
) {
    val x = TranslationState(initialXUnits, initialXMeasurement)
    val y = TranslationState(initialYUnits, initialYMeasurement)
    val z = TranslationState(initialZUnits, initialZMeasurement)

    val position: Position
        get() = Position(x.meters, y.meters, z.meters)
}

@Composable
fun rememberMultiTranslationState(): MultiAxisTranslationState =
    remember { MultiAxisTranslationState() }

@Stable
class MultiAxisRotationState(
    initialXUnits: String = "0",
    initialYUnits: String = "0",
    initialZUnits: String = "0",
) {
    val x = RotationState(initialXUnits)
    val y = RotationState(initialYUnits)
    val z = RotationState(initialZUnits)

    val quaternion: Quaternion
        get() = Quaternion.fromEuler(Float3(x.degrees, y.degrees, z.degrees))
}

@Composable
fun rememberMultiRotationState(
    initialXUnits: String = "0",
    initialYUnits: String = "0",
    initialZUnits: String = "0",
): MultiAxisRotationState = remember { MultiAxisRotationState(initialXUnits, initialYUnits, initialZUnits) }

fun unidirectionalTransformPair(
    axis: Axis,
    translationUnits: Float,
    rotationUnits: Float,
): Pair<Position, Quaternion> = unidirectionalTranslation(axis, translationUnits) to
        unidirectionalRotation(axis, rotationUnits)

fun unidirectionalTranslation(axis: Axis, meters: Float) = axis.unitVector * meters
fun unidirectionalRotation(axis: Axis, degrees: Float) =
    Quaternion.fromEuler(axis.unitVector * degrees)