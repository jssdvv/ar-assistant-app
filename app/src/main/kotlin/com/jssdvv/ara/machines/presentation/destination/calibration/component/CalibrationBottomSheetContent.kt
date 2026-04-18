package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.jssdvv.ara.core.presentation.common.component.DeleteIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.measurement.Measurement
import com.jssdvv.ara.machines.domain.type.measurement.MeasurementMode
import com.jssdvv.ara.machines.domain.type.measurement.Rotation
import com.jssdvv.ara.machines.domain.type.measurement.Translation
import com.jssdvv.ara.machines.presentation.component.OutlinedScrollWheel

@Composable
fun CalibrationBottomSheetContent(
    onRestoreDefaults: () -> Unit,
    measurement: Measurement,
    mode: MeasurementMode,
    onMeasurementChange: (Measurement) -> Unit,
    onModeChange: (MeasurementMode) -> Unit,
    onDeleteModel: () -> Unit,
    onAxisPressed: (Axis, Boolean) -> Unit,
    onTickDragged: (Axis, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium)
            .imePadding(),
        state = state,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Measurement.entries.forEach { measure ->
                    FilterChip(
                        selected = measurement == measure,
                        onClick = { onMeasurementChange(measure) },
                        label = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = FilterChipDefaults.HorizontalSpacing,
                                    alignment = Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(Measurement.TRANSLATION.iconId),
                                    contentDescription = null
                                )
                                Text(stringResource(measure.labelTextId))
                            }
                        },
                        modifier = Modifier.weight(1F),
                    )
                }
            }
        }
        item {
            Button(onClick = onRestoreDefaults) {
                Text(stringResource(id = R.string.button_editor_restore_defaults_action))
            }
        }
        item {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.width(338.dp)
            ) {
                if (measurement == Measurement.TRANSLATION) {
                    Translation.entries.forEachIndexed { index, translation ->
                        SegmentedButton(
                            selected = translation == mode.translation,
                            shape = SegmentedButtonDefaults.itemShape(index, Rotation.entries.size),
                            onClick = { onModeChange(mode.copy(translation = translation)) },
                            label = { Text(text = stringResource(translation.symbolTextId)) }
                        )
                    }
                } else {
                    Rotation.entries.forEachIndexed { index, rotation ->
                        SegmentedButton(
                            selected = rotation == mode.rotation,
                            shape = SegmentedButtonDefaults.itemShape(index, Rotation.entries.size),
                            onClick = { onModeChange(mode.copy(rotation = rotation)) },
                            label = { Text(text = stringResource(rotation.symbolTextId)) }
                        )
                    }
                }
            }
        }
        item {
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
        }
        item{
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
}