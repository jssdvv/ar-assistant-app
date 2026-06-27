package com.jssdvv.ara.machines.presentation.destination.specs.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
import com.jssdvv.ara.machines.presentation.component.DetailListItem
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard

@Composable
fun MotorSpecsCard(
    modifier: Modifier = Modifier,
    motorSpecs: MotorSpecs?,
    editingCard: MachineDetailsCard,
    onClickEditCard: (MachineDetailsCard) -> Unit,
    onClickSaveCard: (MotorSpecs) -> Unit,
) {
    var motorSpecsState by remember { mutableStateOf(MotorSpecs(machineId = 0)) }
    motorSpecs?.let { motorSpecsState = it }

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
                text = "Motor Specifications",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Button(
                onClick = {
                    if (editingCard == MachineDetailsCard.MOTOR_SPECS) {
                        onClickSaveCard(motorSpecsState)
                    } else {
                        onClickEditCard(MachineDetailsCard.MOTOR_SPECS)
                    }
                },
                enabled = editingCard in setOf(
                    MachineDetailsCard.NONE,
                    MachineDetailsCard.MOTOR_SPECS
                ),
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(
                        if (editingCard == MachineDetailsCard.MOTOR_SPECS) {
                            R.string.button_save_action
                        } else {
                            R.string.button_edit_action
                        }
                    )
                )
            }
        }
        if (editingCard == MachineDetailsCard.MOTOR_SPECS) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.effClass ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(effClass = it) },
                    label = { Text(stringResource(R.string.text_field_motor_efficiency_class_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_efficiency_class_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.phasesNumber?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*$"))) {
                            motorSpecsState = motorSpecsState.copy(phasesNumber = it.toIntOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_phases_number_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_phases_number_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.nominalPower?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            motorSpecsState =
                                motorSpecsState.copy(nominalPower = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_nominal_power_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_power),
                            contentDescription = stringResource(R.string.icon_motor_nominal_power_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.frequency?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            motorSpecsState = motorSpecsState.copy(frequency = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_frequency_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_frequency_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.rpm?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*$"))) {
                            motorSpecsState = motorSpecsState.copy(rpm = it.toIntOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_rpm_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_speed),
                            contentDescription = stringResource(R.string.icon_motor_rpm_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.rpmRange ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(rpmRange = it) },
                    label = { Text(stringResource(R.string.text_field_motor_rpm_range_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_rpm_range_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.nominalVoltage ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(nominalVoltage = it) },
                    label = { Text(stringResource(R.string.text_field_motor_nominal_voltage_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_nominal_voltage_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.nominalCurrent ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(nominalCurrent = it) },
                    label = { Text(stringResource(R.string.text_field_motor_nominal_current_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_nominal_current_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.serviceFactor?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            motorSpecsState =
                                motorSpecsState.copy(serviceFactor = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_service_factor_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_service_factor_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.powerFactor?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            motorSpecsState =
                                motorSpecsState.copy(powerFactor = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_power_factor_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_power_factor_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.duty ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(duty = it) },
                    label = { Text(stringResource(R.string.text_field_motor_duty_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_duty_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.roomTemp?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            motorSpecsState = motorSpecsState.copy(roomTemp = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_room_temp_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_heat),
                            contentDescription = stringResource(R.string.icon_motor_room_temp_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.energyEff?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            motorSpecsState = motorSpecsState.copy(energyEff = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_energy_efficiency_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_energy_efficiency_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.maxAltitude?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*$"))) {
                            motorSpecsState = motorSpecsState.copy(maxAltitude = it.toIntOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_max_altitude_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_max_altitude_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.ingressProtection ?: "",
                    onValueChange = {
                        motorSpecsState = motorSpecsState.copy(ingressProtection = it)
                    },
                    label = { Text(stringResource(R.string.text_field_motor_ingress_protection_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_ingress_protection_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.mountingType ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(mountingType = it) },
                    label = { Text(stringResource(R.string.text_field_motor_mounting_type_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_mounting_type_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.frameType ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(frameType = it) },
                    label = { Text(stringResource(R.string.text_field_motor_frame_type_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_frame_type_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.coolingMethod ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(coolingMethod = it) },
                    label = { Text(stringResource(R.string.text_field_motor_cooling_method_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_cooling_method_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.driveEnd ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(driveEnd = it) },
                    label = { Text(stringResource(R.string.text_field_motor_drive_end_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_drive_end_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.nonDriveEnd ?: "",
                    onValueChange = { motorSpecsState = motorSpecsState.copy(nonDriveEnd = it) },
                    label = { Text(stringResource(R.string.text_field_motor_non_drive_end_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_non_drive_end_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.insulationClass ?: "",
                    onValueChange = {
                        motorSpecsState = motorSpecsState.copy(insulationClass = it)
                    },
                    label = { Text(stringResource(R.string.text_field_motor_insulation_class_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_insulation_class_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.insulationTemp?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            motorSpecsState =
                                motorSpecsState.copy(insulationTemp = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_insulation_class_temp_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(R.string.icon_motor_insulation_class_temp_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorSpecsState.weight?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            motorSpecsState = motorSpecsState.copy(weight = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_motor_weight_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_weight),
                            contentDescription = stringResource(R.string.icon_motor_weight_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        } else {
            motorSpecs?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    listOf(
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_efficiency_class_label,
                            motorSpecs.effClass
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_phases_number_label,
                            motorSpecs.phasesNumber?.toString()
                        ),
                        Triple(
                            R.drawable.ic_power,
                            R.string.text_field_motor_nominal_power_label,
                            motorSpecs.nominalPower?.toString()
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_frequency_label,
                            motorSpecs.frequency?.toString()
                        ),
                        Triple(
                            R.drawable.ic_speed,
                            R.string.text_field_motor_rpm_label,
                            motorSpecs.rpm?.toString()
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_rpm_range_label,
                            motorSpecs.rpmRange
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_nominal_voltage_label,
                            motorSpecs.nominalVoltage
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_nominal_current_label,
                            motorSpecs.nominalCurrent
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_service_factor_label,
                            motorSpecs.serviceFactor?.toString()
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_power_factor_label,
                            motorSpecs.powerFactor?.toString()
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_duty_label,
                            motorSpecs.duty
                        ),
                        Triple(
                            R.drawable.ic_heat,
                            R.string.text_field_motor_room_temp_label,
                            motorSpecs.roomTemp?.toString()
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_energy_efficiency_label,
                            motorSpecs.energyEff?.toString()
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_max_altitude_label,
                            motorSpecs.maxAltitude?.toString()
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_ingress_protection_label,
                            motorSpecs.ingressProtection
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_mounting_type_label,
                            motorSpecs.mountingType
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_frame_type_label,
                            motorSpecs.frameType
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_cooling_method_label,
                            motorSpecs.coolingMethod
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_drive_end_label,
                            motorSpecs.driveEnd
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_non_drive_end_label,
                            motorSpecs.nonDriveEnd
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_insulation_class_label,
                            motorSpecs.insulationClass
                        ),
                        Triple(
                            R.drawable.ic_text,
                            R.string.text_field_motor_insulation_class_temp_label,
                            motorSpecs.insulationTemp?.toString()
                        ),
                        Triple(
                            R.drawable.ic_weight,
                            R.string.text_field_motor_weight_label,
                            motorSpecs.weight?.toString()
                        ),
                    ).forEach { (icon, label, value) ->
                        DetailListItem(
                            painter = painterResource(icon),
                            headline = stringResource(label),
                            text = value ?: "N/A"
                        )
                    }
                }
            } ?: Box(
                Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.card_machine_identity_not_available_message),
                )
            }
        }
    }
}