package com.jssdvv.ara.machines.presentation.destination.specs.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.TextIcon
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.machines.presentation.component.DetailListItem
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard

@Composable
fun MachineSpecsCard(
    modifier: Modifier = Modifier,
    machineSpecs: MachineSpecs?,
    editingCard: MachineDetailsCard,
    onClickEditCard: (MachineDetailsCard) -> Unit,
    onClickSaveCard: (MachineSpecs) -> Unit,
) {
    var machineSpecsState by remember { mutableStateOf(MachineSpecs(machineId = 0)) }
    machineSpecs?.let { machineSpecsState = it }

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
                text = "Machine Specifications",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Button(
                onClick = {
                    if (editingCard == MachineDetailsCard.MACHINE_SPECS) {
                        onClickSaveCard(machineSpecsState)
                    } else {
                        onClickEditCard(MachineDetailsCard.MACHINE_SPECS)
                    }
                },
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 8.dp,
                    end = 24.dp,
                    bottom = 8.dp
                ),
                enabled = editingCard in setOf(
                    MachineDetailsCard.NONE,
                    MachineDetailsCard.MACHINE_SPECS
                )
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(
                        if (editingCard == MachineDetailsCard.MACHINE_SPECS) {
                            R.string.button_save_action
                        } else {
                            R.string.button_edit_action
                        }
                    )
                )
            }
        }
        if (editingCard == MachineDetailsCard.MACHINE_SPECS) {

            // Order of the fields:
            // 01. Job Description
            // 02. Service Capacity
            // 03. Speed
            // 04. Lubricant
            // 05. Power Supply
            // 06. Weight
            // 07. Height
            // 08. Width
            // 09. Length
            // 10. Hours Per Day
            // 11. Room Temp
            // 12. Additional Description

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.jobDesc ?: "",
                    onValueChange = { machineSpecsState = machineSpecsState.copy(jobDesc = it) },
                    label = { Text(stringResource(R.string.text_field_machine_job_desc_label)) },
                    leadingIcon = {
                        TextIcon(
                            contentDescription = stringResource(R.string.icon_machine_job_desc_content_desc),
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.serviceCapacity ?: "",
                    onValueChange = {
                        machineSpecsState = machineSpecsState.copy(serviceCapacity = it)
                    },
                    label = { Text(stringResource(R.string.text_field_machine_service_capacity_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_capacity),
                            contentDescription = stringResource(R.string.icon_machine_service_capacity_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.speed ?: "",
                    onValueChange = { machineSpecsState = machineSpecsState.copy(speed = it) },
                    label = { Text(stringResource(R.string.text_field_machine_speed_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_speed),
                            contentDescription = stringResource(R.string.icon_machine_speed_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.lubricant ?: "",
                    onValueChange = { machineSpecsState = machineSpecsState.copy(lubricant = it) },
                    label = { Text(stringResource(R.string.text_field_machine_lubricant_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_lubricant),
                            contentDescription = stringResource(R.string.icon_machine_lubricant_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.powerSupply ?: "",
                    onValueChange = {
                        machineSpecsState = machineSpecsState.copy(powerSupply = it)
                    },
                    label = { Text(stringResource(R.string.text_field_machine_power_supply_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_power),
                            contentDescription = stringResource(R.string.icon_machine_power_supply_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.weight ?: "",
                    onValueChange = { machineSpecsState = machineSpecsState.copy(weight = it) },
                    label = { Text(stringResource(R.string.text_field_machine_weight_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_weight),
                            contentDescription = stringResource(R.string.icon_machine_weight_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.height ?: "",
                    onValueChange = { machineSpecsState = machineSpecsState.copy(height = it) },
                    label = { Text(stringResource(R.string.text_field_machine_height_label)) },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.rotate(90F),
                            painter = painterResource(R.drawable.ic_arrow_range),
                            contentDescription = stringResource(R.string.icon_machine_height_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.width ?: "",
                    onValueChange = { machineSpecsState = machineSpecsState.copy(width = it) },
                    label = { Text(stringResource(R.string.text_field_machine_width_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_range),
                            contentDescription = stringResource(R.string.icon_machine_weight_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.length ?: "",
                    onValueChange = { machineSpecsState = machineSpecsState.copy(length = it) },
                    label = { Text(stringResource(R.string.text_field_machine_length_label)) },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.rotate(45F),
                            painter = painterResource(R.drawable.ic_arrow_range),
                            contentDescription = stringResource(R.string.icon_machine_length_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.hoursPerDay?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\$"))) {
                            machineSpecsState = machineSpecsState.copy(
                                hoursPerDay = it.toIntOrNull()
                            )
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_machine_hours_per_day_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_hours),
                            contentDescription = stringResource(R.string.icon_machine_hours_per_day_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.roomTemp ?: "",
                    onValueChange = { machineSpecsState = machineSpecsState.copy(roomTemp = it) },
                    label = { Text(stringResource(R.string.text_field_machine_room_temp_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_heat),
                            contentDescription = stringResource(R.string.icon_machine_room_temp_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = machineSpecsState.additionalDesc ?: "",
                    onValueChange = {
                        machineSpecsState = machineSpecsState.copy(additionalDesc = it)
                    },
                    label = { Text(stringResource(R.string.text_field_machine_additional_desc_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_unknown),
                            contentDescription = stringResource(R.string.icon_machine_additional_desc_content_desc)
                        )
                    }
                )
            }
        } else {
            machineSpecs?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    listOf(
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_machine_job_desc_label,
                            machineSpecs.jobDesc
                        ),
                        Triple(
                            R.drawable.ic_capacity,
                            R.string.text_field_machine_service_capacity_label,
                            machineSpecs.serviceCapacity
                        ),
                        Triple(
                            R.drawable.ic_speed,
                            R.string.text_field_machine_speed_label,
                            machineSpecs.speed
                        ),
                        Triple(
                            R.drawable.ic_lubricant,
                            R.string.text_field_machine_lubricant_label,
                            machineSpecs.lubricant
                        ),
                        Triple(
                            R.drawable.ic_power,
                            R.string.text_field_machine_power_supply_label,
                            machineSpecs.powerSupply
                        ),
                        Triple(
                            R.drawable.ic_weight,
                            R.string.text_field_machine_weight_label,
                            machineSpecs.weight
                        )
                    ).forEach { (icon, label, value) ->
                        DetailListItem(
                            painter = painterResource(icon),
                            headline = stringResource(label),
                            text = value ?: "N/A"
                        )
                    }
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_arrow_range),
                        headline = stringResource(R.string.text_field_machine_height_label),
                        text = machineSpecs.height ?: "N/A",
                        iconRotation = 90F
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_arrow_range),
                        headline = stringResource(R.string.text_field_machine_width_label),
                        text = machineSpecs.width ?: "N/A"
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_arrow_range),
                        headline = stringResource(R.string.text_field_machine_length_label),
                        text = machineSpecs.length ?: "N/A",
                        iconRotation = 45F
                    )

                    listOf(
                        Triple(
                            R.drawable.ic_hours,
                            R.string.text_field_machine_hours_per_day_label,
                            machineSpecs.hoursPerDay
                        ),
                        Triple(
                            R.drawable.ic_heat,
                            R.string.text_field_machine_room_temp_label,
                            machineSpecs.roomTemp
                        ),
                        Triple(
                            R.drawable.ic_unknown,
                            R.string.text_field_machine_additional_desc_label,
                            machineSpecs.additionalDesc
                        )
                    ).forEach { (icon, label, value) ->
                        DetailListItem(
                            painter = painterResource(icon),
                            headline = stringResource(label),
                            text = value?.toString() ?: "N/A"
                        )
                    }
                }
            } ?: Box(
                Modifier.fillMaxSize()
            ) {
                Text(
                    text = "No hay información de la máquina disponible",
                )
            }
        }
    }
}