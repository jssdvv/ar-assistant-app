package com.jssdvv.ara.schedule.presentation.destination.events.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.formatMedium
import com.jssdvv.ara.core.presentation.foundation.component.DatePickerModal
import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineActivities
import com.jssdvv.ara.schedule.domain.model.Event
import com.jssdvv.ara.schedule.domain.type.RecurrenceUnit
import com.jssdvv.ara.schedule.presentation.destination.events.EventDialogState
import java.time.LocalDate

@Composable
fun CreateEventDialog(
    selectedDate: LocalDate,
    dialogState: EventDialogState,
    onMachineSelected: (Machine) -> Unit,
    onActivitySelected: (Activity) -> Unit,
    onConfirm: (Event) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(selectedDate) }
    var recurrent by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf("1") }
    var recurrenceUnit by remember { mutableStateOf(RecurrenceUnit.ONCE) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerModal(
            currentDateSelected = date,
            onDateSelected = {
                date = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_event_create_title)) },
        text = {
            EventForm(
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                date = date,
                onDateChange = { date = it },
                recurrent = recurrent,
                onRecurrentChange = { recurrent = it },
                quantity = quantity,
                onQuantityChange = { quantity = it },
                recurrenceUnit = recurrenceUnit,
                onRecurrenceUnitChange = { recurrenceUnit = it },
                showDatePicker = showDatePicker,
                onShowDatePicker = { showDatePicker = it },
                machinesWithActivities = dialogState.machinesWithActivities,
                selectedMachine = dialogState.selectedMachine,
                selectedActivity = dialogState.selectedActivity,
                onMachineSelected = onMachineSelected,
                onActivitySelected = onActivitySelected,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Event(
                            activityId = 0,
                            title = title,
                            description = description.ifBlank { null },
                            date = date,
                            recurrent = recurrent,
                            quantity = quantity.toIntOrNull() ?: 1,
                            recurrenceUnit = recurrenceUnit
                        )
                    )
                },
                enabled = title.isNotBlank() && dialogState.selectedActivity != null
            ) {
                Text(stringResource(R.string.button_event_save_action))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_event_cancel_action))
            }
        }
    )
}

@Composable
fun EditEventDialog(
    event: Event,
    dialogState: EventDialogState,
    onMachineSelected: (Machine) -> Unit,
    onActivitySelected: (Activity) -> Unit,
    onConfirm: (Event) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf(event.title) }
    var description by remember { mutableStateOf(event.description ?: "") }
    var date by remember { mutableStateOf(event.date) }
    var recurrent by remember { mutableStateOf(event.recurrent) }
    var quantity by remember { mutableStateOf(event.quantity.toString()) }
    var recurrenceUnit by remember { mutableStateOf(event.recurrenceUnit) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerModal(
            currentDateSelected = date,
            onDateSelected = {
                date = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_event_edit_title)) },
        text = {
            EventForm(
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                date = date,
                onDateChange = { date = it },
                recurrent = recurrent,
                onRecurrentChange = { recurrent = it },
                quantity = quantity,
                onQuantityChange = { quantity = it },
                recurrenceUnit = recurrenceUnit,
                onRecurrenceUnitChange = { recurrenceUnit = it },
                showDatePicker = showDatePicker,
                onShowDatePicker = { showDatePicker = it },
                machinesWithActivities = dialogState.machinesWithActivities,
                selectedMachine = dialogState.selectedMachine,
                selectedActivity = dialogState.selectedActivity,
                onMachineSelected = onMachineSelected,
                onActivitySelected = onActivitySelected,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        event.copy(
                            title = title,
                            description = description.ifBlank { null },
                            date = date,
                            recurrent = recurrent,
                            quantity = quantity.toIntOrNull() ?: 1,
                            recurrenceUnit = recurrenceUnit
                        )
                    )
                },
                enabled = title.isNotBlank()
            ) {
                Text(stringResource(R.string.button_event_save_action))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_event_cancel_action))
            }
        }
    )
}

@Composable
fun DeleteEventDialog(
    event: Event,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_event_delete_title)) },
        text = { Text(stringResource(R.string.dialog_event_delete_body)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.button_event_delete_action))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_event_cancel_action))
            }
        }
    )
}

@Composable
private fun EventForm(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    recurrent: Boolean,
    onRecurrentChange: (Boolean) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    recurrenceUnit: RecurrenceUnit,
    onRecurrenceUnitChange: (RecurrenceUnit) -> Unit,
    showDatePicker: Boolean,
    onShowDatePicker: (Boolean) -> Unit,
    machinesWithActivities: List<MachineActivities>,
    selectedMachine: Machine?,
    selectedActivity: Activity?,
    onMachineSelected: (Machine) -> Unit,
    onActivitySelected : (Activity) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.form_event_title_label)) },
            singleLine = true
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.form_event_description_label)) },
            minLines = 2,
            maxLines = 4
        )

        // Selector de Machine
        MachineDropdown(
            machines = machinesWithActivities.map { it.machine },
            selected = selectedMachine,
            onSelected = onMachineSelected
        )

        // Selector de Activity — solo visible si hay machine seleccionada
        val availableActivities = machinesWithActivities
            .firstOrNull { it.machine.id == selectedMachine?.id }
            ?.activities
            ?: emptyList()

        AnimatedVisibility(visible = selectedMachine != null) {
            ActivityDropdown(
                activities = availableActivities,
                selected = selectedActivity,
                onSelected = onActivitySelected
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDatePicker(true) },
            value = date.formatMedium(),
            onValueChange = {},
            label = { Text(stringResource(R.string.form_event_date_label)) },
            readOnly = true,
            enabled = false,
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_date),
                    contentDescription = null
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.form_event_recurrent_label),
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = recurrent,
                onCheckedChange = onRecurrentChange
            )
        }
        AnimatedVisibility(
            visible = recurrent,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = quantity,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*$"))) onQuantityChange(it)
                    },
                    label = { Text(stringResource(R.string.form_event_every_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                RecurrenceUnitDropdown(
                    modifier = Modifier.weight(1f),
                    selected = recurrenceUnit,
                    onSelected = onRecurrenceUnitChange
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MachineDropdown(
    machines: List<Machine>,
    selected: Machine?,
    onSelected: (Machine) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.form_event_machine_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            machines.forEach { machine ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = machine.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = machine.code,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onSelected(machine)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityDropdown(
    activities: List<Activity>,
    selected: Activity?,
    onSelected: (Activity) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.form_event_activity_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            activities.forEach { activity ->
                DropdownMenuItem(
                    text = { Text(activity.name) },
                    onClick = {
                        onSelected(activity)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurrenceUnitDropdown(
    selected: RecurrenceUnit,
    onSelected: (RecurrenceUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selected.name.lowercase().replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.form_event_unit_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            RecurrenceUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}