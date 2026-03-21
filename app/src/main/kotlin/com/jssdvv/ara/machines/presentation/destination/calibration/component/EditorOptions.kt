package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.CheckIcon
import com.jssdvv.ara.core.presentation.common.CloseIcon
import com.jssdvv.ara.machines.presentation.component.OutlinedScrollWheel
import com.jssdvv.ara.machines.presentation.destination.calibration.function.RotationMode
import com.jssdvv.ara.machines.presentation.destination.calibration.function.Transformation
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TransformationMode
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TranslationMode

@Composable
fun EditorOptions(
    onRestoreDefaults: () -> Unit,
    transformation: Transformation,
    transformationMode: TransformationMode,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onTransformationChange: (Transformation) -> Unit,
    onTransformationModeChange: (TransformationMode) -> Unit,
    onXTickDragged: (Int) -> Unit,
    onYTickDragged: (Int) -> Unit,
    onZTickDragged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotationModes = RotationMode.entries
    val translationModes = TranslationMode.entries

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Transformation Chip Options
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterChip(
                    selected = transformation == Transformation.TRANSLATION,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Transformation.TRANSLATION.iconId),
                            contentDescription = null
                        )
                    },
                    onClick = { onTransformationChange(Transformation.TRANSLATION) },
                    label = { Text(stringResource(R.string.transformation_mode_translation_label)) }
                )
                FilterChip(
                    selected = transformation == Transformation.ROTATION,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Transformation.ROTATION.iconId),
                            contentDescription = null
                        )
                    },
                    onClick = { onTransformationChange(Transformation.ROTATION) },
                    label = { Text(stringResource(R.string.transformation_mode_rotation_label) ) }
                )
            }

            Row {
                IconButton(onCancelClick) { CloseIcon() }
                IconButton(onSaveClick) { CheckIcon() }
            }
        }

        Button(onClick = onRestoreDefaults) {
            Text(text = "Restaurar valor predeterminado") // TODO create string
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.width(338.dp)
        ) {
            if (transformation == Transformation.TRANSLATION) {
                translationModes.forEachIndexed { index, translationMode ->
                    SegmentedButton(
                        selected = translationMode == transformationMode.translation,
                        shape = SegmentedButtonDefaults.itemShape(index, rotationModes.size),
                        onClick = {
                            onTransformationModeChange(
                                transformationMode.copy(translation = translationMode)
                            )
                        },
                        label = { Text(text = stringResource(translationMode.symbolTextId)) }
                    )
                }
            } else {
                rotationModes.forEachIndexed { index, rotationMode ->
                    SegmentedButton(
                        selected = rotationMode == transformationMode.rotation,
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = rotationModes.size
                        ),
                        onClick = {
                            onTransformationModeChange(
                                transformationMode.copy(rotation = rotationMode)
                            )
                        },
                        label = { Text(text = stringResource(rotationMode.symbolTextId)) }
                    )
                }
            }
        }

        Column {
            // Scroll Wheels
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubtractButton { onXTickDragged(-1) }
                OutlinedScrollWheel(colorResource(R.color.gizmo_x)) { onXTickDragged(it) }
                AddButton { onXTickDragged(1) }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                SubtractButton { onZTickDragged(-1) }
                OutlinedScrollWheel(colorResource(R.color.gizmo_z)) { onZTickDragged(it) }
                AddButton { onZTickDragged(1) }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                SubtractButton { onYTickDragged(-1) }
                OutlinedScrollWheel(colorResource(R.color.gizmo_y)) { onYTickDragged(it) }
                AddButton { onYTickDragged(1) }
            }
        }
    }
}