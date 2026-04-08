package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.jssdvv.ara.core.presentation.common.DeleteIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.component.OutlinedScrollWheel
import com.jssdvv.ara.machines.presentation.destination.calibration.function.RotationMode
import com.jssdvv.ara.machines.presentation.destination.calibration.function.Transformation
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TransformationMode
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TranslationMode

@Composable
fun CalibrationBottomSheetContent(
    onRestoreDefaults: () -> Unit,
    transformation: Transformation,
    mode: TransformationMode,
    onTransformationChange: (Transformation) -> Unit,
    onModeChange: (TransformationMode) -> Unit,
    onDeleteModel: () -> Unit,
    onAxisPressed: (Axis, Boolean) -> Unit,
    onTickDragged: (Axis, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotationModes = RotationMode.entries
    val translationModes = TranslationMode.entries

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Transformation Chip Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterChip(
                selected = transformation == Transformation.TRANSLATION,
                onClick = { onTransformationChange(Transformation.TRANSLATION) },
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Transformation.TRANSLATION.iconId),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(FilterChipDefaults.HorizontalSpacing))
                        Text(stringResource(R.string.transformation_mode_translation_label))
                    }
                },
                modifier = Modifier.weight(1F),
            )
            FilterChip(
                selected = transformation == Transformation.ROTATION,
                onClick = { onTransformationChange(Transformation.ROTATION) },
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Transformation.ROTATION.iconId),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(FilterChipDefaults.HorizontalSpacing))
                        Text(stringResource(R.string.transformation_mode_rotation_label))
                    }
                },
                modifier = Modifier.weight(1F),
            )
        }

        Button(onClick = onRestoreDefaults) {
            Text(stringResource(id = R.string.button_editor_restore_defaults_action))
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.width(338.dp)
        ) {
            if (transformation == Transformation.TRANSLATION) {
                translationModes.forEachIndexed { index, translationMode ->
                    SegmentedButton(
                        selected = translationMode == mode.translation,
                        shape = SegmentedButtonDefaults.itemShape(index, rotationModes.size),
                        onClick = {
                            onModeChange(mode.copy(translation = translationMode))
                        },
                        label = { Text(text = stringResource(translationMode.symbolTextId)) }
                    )
                }
            } else {
                rotationModes.forEachIndexed { index, rotationMode ->
                    SegmentedButton(
                        selected = rotationMode == mode.rotation,
                        shape = SegmentedButtonDefaults.itemShape(index, rotationModes.size),
                        onClick = {
                            onModeChange(mode.copy(rotation = rotationMode))
                        },
                        label = { Text(text = stringResource(rotationMode.symbolTextId)) }
                    )
                }
            }
        }

        // Scroll Wheels
        Column {
            listOf(
                Axis.X to colorResource(R.color.gizmo_x),
                Axis.Z to colorResource(R.color.gizmo_z),
                Axis.Y to colorResource(R.color.gizmo_y)
            ).forEach { (axis, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SubtractButton { onTickDragged(axis, -1) }
                    OutlinedScrollWheel(
                        color = color,
                        onPressedChange = { onAxisPressed(axis, it) },
                        onDrag = { onTickDragged(axis, it) }
                    )
                    AddButton { onTickDragged(axis, 1) }
                }
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        ButtonWithIcon(
            onClick = onDeleteModel,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            icon = { DeleteIcon() },
            content = { Text("Delete model") } // todo create string
        )
    }
}