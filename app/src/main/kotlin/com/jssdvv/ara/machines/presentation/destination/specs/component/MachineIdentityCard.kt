package com.jssdvv.ara.machines.presentation.destination.specs.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.CheckIcon
import com.jssdvv.ara.core.presentation.foundation.component.DatePickerModal
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.type.MachineType
import com.jssdvv.ara.machines.presentation.component.DetailListItem
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachineIdentityCard(
    modifier: Modifier = Modifier,
    machine: Machine?,
    editingCard: MachineDetailsCard,
    onClickEditCard: (MachineDetailsCard) -> Unit,
    onClickSaveCard: (Machine) -> Unit,
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    var machineState by remember {
        mutableStateOf(
            Machine(
                code = "",
                name = "",
                type = MachineType.UNKNOWN,
                createdAt = Date(0L),
                modifiedAt = Date(0L)
            )
        )
    }
    machine?.let { machineState = it }
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Identification data",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Button(
                onClick = {
                    if (editingCard == MachineDetailsCard.MACHINE) {
                        onClickSaveCard(machineState)
                    } else {
                        onClickEditCard(MachineDetailsCard.MACHINE)
                    }
                },
                enabled = editingCard in setOf(
                    MachineDetailsCard.NONE,
                    MachineDetailsCard.MACHINE
                ),
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(
                        if (editingCard == MachineDetailsCard.MACHINE) {
                            R.string.button_save_action
                        } else {
                            R.string.button_edit_action
                        }
                    )
                )
            }
        }
        if (editingCard == MachineDetailsCard.MACHINE) {
            var showDatePickerDialog by remember { mutableStateOf(false) }
            var showTypeDropDownMenu by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineState.code,
                    onValueChange = { machineState = machineState.copy(code = it) },
                    label = { Text(stringResource(R.string.text_field_code_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_code),
                            contentDescription = stringResource(R.string.icon_machine_code_content_desc)
                        )
                    }
                )
                ExposedDropdownMenuBox (
                    expanded = showTypeDropDownMenu,
                    onExpandedChange = { showTypeDropDownMenu = it }
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isTextFieldPressed by interactionSource.collectIsPressedAsState()
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(interactionSource, null) {
                                showTypeDropDownMenu = !showTypeDropDownMenu
                            }
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
                        value = stringResource(machineState.type.labelResId),
                        onValueChange = {},
                        label = { Text(stringResource(R.string.text_field_type_label)) },
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(machineState.type.iconResId),
                                contentDescription = stringResource(machineState.type.iconContentDescResId)
                            )
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = showTypeDropDownMenu,
                        onDismissRequest = {
                            if (!isTextFieldPressed) {
                                showTypeDropDownMenu = false
                            }
                        }
                    ) {
                        MachineType.entries.forEach { machineType ->
                            DropdownMenuItem(
                                text = { Text(stringResource(machineType.labelResId)) },
                                onClick = {
                                    machineState = machineState.copy(type = machineType)
                                    showTypeDropDownMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(machineType.iconResId),
                                        contentDescription = stringResource(machineType.iconContentDescResId)
                                    )
                                },
                                trailingIcon = {
                                    if (machineState.type == machineType) CheckIcon()
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineState.location ?: "",
                    onValueChange = { machineState = machineState.copy(location = it) },
                    label = { Text(stringResource(R.string.text_field_location_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = ""
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineState.brand ?: "",
                    onValueChange = { machineState = machineState.copy(brand = it) },
                    label = { Text(stringResource(R.string.text_field_brand_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_brand),
                            contentDescription = ""
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineState.model ?: "",
                    onValueChange = { machineState = machineState.copy(model = it) },
                    label = { Text(stringResource(R.string.text_field_model_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_model),
                            contentDescription = ""
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineState.serial ?: "",
                    onValueChange = { machineState = machineState.copy(serial = it) },
                    label = { Text(stringResource(R.string.text_field_serial_number_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_serial_number),
                            contentDescription = ""
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineState.fabricationYear?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d{0,4}$"))) {
                            machineState =
                                machineState.copy(fabricationYear = it.toIntOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_fabrication_year_label)) },
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_year),
                            contentDescription = ""
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineState.price?.toString() ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(R.string.text_field_price_label)) },
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_price),
                            contentDescription = ""
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(machineState.acquisitionDate) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent = waitForUpOrCancellation(
                                    pass = PointerEventPass.Initial
                                )
                                if (upEvent != null) showDatePickerDialog = true
                            }
                        },
                    value = machineState.acquisitionDate?.let { date ->
                        dateFormat.format(
                            date
                        )
                    } ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(R.string.text_field_acquisition_date_label)) },
                    maxLines = 1,
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_date),
                            contentDescription = ""
                        )
                    }
                )
                if (showDatePickerDialog) {
                    DatePickerModal(
                        currentDateSelected = machineState.acquisitionDate,
                        onDateSelected = {
                            machineState = machineState.copy(acquisitionDate = it)
                        },
                        onDismiss = { showDatePickerDialog = false },
                    )
                }
            }
        } else {
            machine?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_code),
                        headline = stringResource(R.string.text_field_code_label),
                        text = machine.code
                    )
                    DetailListItem(
                        painter = painterResource(machine.type.iconResId),
                        headline = stringResource(R.string.text_field_type_label),
                        text = stringResource(machine.type.labelResId)
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_location),
                        headline = stringResource(R.string.text_field_location_label),
                        text = machine.location ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_brand),
                        headline = stringResource(R.string.text_field_brand_label),
                        text = machine.brand ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_model),
                        headline = stringResource(R.string.text_field_model_label),
                        text = machine.model ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_serial_number),
                        headline = stringResource(R.string.text_field_serial_number_label),
                        text = machine.serial ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_year),
                        headline = stringResource(R.string.text_field_fabrication_year_label),
                        text = machine.fabricationYear?.toString() ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_price),
                        headline = stringResource(R.string.text_field_price_label),
                        text = machine.price?.toString() ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_date),
                        headline = stringResource(R.string.text_field_acquisition_date_label),
                        text = machine.acquisitionDate?.let { dateFormat.format(it) } ?: ""
                    )
                }
            } ?: Box(
                Modifier.fillMaxSize()
            ) {
                Text(
                    text = "No hay información de la máquina disponible", // todo crear string
                )
            }
        }
    }
}