package com.jssdvv.ara.machines.presentation.destination.specs.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.jssdvv.ara.core.domain.utility.formatMedium
import com.jssdvv.ara.core.presentation.foundation.component.DatePickerModal
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.presentation.component.DetailListItem
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorIdentityCard(
    modifier: Modifier = Modifier,
    motorIdentity: MotorIdentity?,
    editingCard: MachineDetailsCard,
    onClickEditCard: (MachineDetailsCard) -> Unit,
    onClickSaveCard: (MotorIdentity) -> Unit,
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    var motorState by remember { mutableStateOf(MotorIdentity(machineId = 0)) }
    motorIdentity?.let { motorState = it }
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
                text = "Motor Identification",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Button(
                onClick = {
                    if (editingCard == MachineDetailsCard.MOTOR) {
                        onClickSaveCard(motorState)
                    } else {
                        onClickEditCard(MachineDetailsCard.MOTOR)
                    }
                },
                enabled = editingCard in setOf(
                    MachineDetailsCard.NONE,
                    MachineDetailsCard.MOTOR
                ),
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(
                        if (editingCard == MachineDetailsCard.MOTOR) {
                            R.string.button_save_action
                        } else {
                            R.string.button_edit_action
                        }
                    )
                )
            }
        }
        if (editingCard == MachineDetailsCard.MOTOR) {
            var showDatePickerDialog by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorState.brand ?: "",
                    onValueChange = { motorState = motorState.copy(brand = it) },
                    label = { Text(stringResource(R.string.text_field_brand_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_brand),
                            contentDescription = stringResource(R.string.icon_motor_brand_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorState.model ?: "",
                    onValueChange = { motorState = motorState.copy(model = it) },
                    label = { Text(stringResource(R.string.text_field_model_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_model),
                            contentDescription = stringResource(R.string.icon_motor_model_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorState.serialNumber ?: "",
                    onValueChange = { motorState = motorState.copy(serialNumber = it) },
                    label = { Text(stringResource(R.string.text_field_serial_number_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_serial_number),
                            contentDescription = stringResource(R.string.icon_motor_serial_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorState.productNumber ?: "",
                    onValueChange = { motorState = motorState.copy(productNumber = it) },
                    label = { Text(stringResource(R.string.text_field_product_number_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_product_number),
                            contentDescription = stringResource(R.string.icon_motor_product_number_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorState.fabricationCountry ?: "",
                    onValueChange = { motorState = motorState.copy(fabricationCountry = it) },
                    label = { Text(stringResource(R.string.text_field_fabrication_country_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_country),
                            contentDescription = stringResource(R.string.icon_motor_fabrication_country_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorState.standards ?: "",
                    onValueChange = { motorState = motorState.copy(standards = it) },
                    label = { Text(stringResource(R.string.text_field_standards_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_standard),
                            contentDescription = stringResource(R.string.icon_motor_standards_applied_content_desc)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorState.fabricationYear?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d{0,4}$"))) {
                            motorState = motorState.copy(fabricationYear = it.toIntOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_fabrication_year_label)) },
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_year),
                            contentDescription = stringResource(R.string.icon_motor_fabrication_year_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motorState.price?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            motorState = motorState.copy(price = it.toDoubleOrNull())
                        }
                    },
                    label = { Text(stringResource(R.string.text_field_price_label)) },
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_price),
                            contentDescription = stringResource(R.string.icon_motor_price_content_desc)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(motorState.acquisitionDate) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent = waitForUpOrCancellation(
                                    pass = PointerEventPass.Initial
                                )
                                if (upEvent != null) showDatePickerDialog = true
                            }
                        },
                    // TODO: fix crash on edit on date format
                    value = motorState.acquisitionDate?.formatMedium() ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(R.string.text_field_acquisition_date_label)) },
                    maxLines = 1,
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_date),
                            contentDescription = stringResource(R.string.icon_motor_acquisition_date_content_desc)
                        )
                    }
                )
                if (showDatePickerDialog) {
                    DatePickerModal(
                        currentDateSelected = motorState.acquisitionDate,
                        onDateSelected = {
                            motorState = motorState.copy(acquisitionDate = it)
                        },
                        onDismiss = { showDatePickerDialog = false }
                    )
                }
            }
        } else {
            motorIdentity?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_brand),
                        headline = stringResource(R.string.text_field_brand_label),
                        text = motorIdentity.brand ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_model),
                        headline = stringResource(R.string.text_field_model_label),
                        text = motorIdentity.model ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_serial_number),
                        headline = stringResource(R.string.text_field_serial_number_label),
                        text = motorIdentity.serialNumber ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_product_number),
                        headline = stringResource(R.string.text_field_product_number_label),
                        text = motorIdentity.productNumber ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_country),
                        headline = stringResource(R.string.text_field_fabrication_country_label),
                        text = motorIdentity.fabricationCountry ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_standard),
                        headline = stringResource(R.string.text_field_standards_label),
                        text = motorIdentity.standards ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_year),
                        headline = stringResource(R.string.text_field_fabrication_year_label),
                        text = motorIdentity.fabricationYear?.toString() ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_price),
                        headline = stringResource(R.string.text_field_price_label),
                        text = motorIdentity.price?.toString() ?: ""
                    )
                    DetailListItem(
                        painter = painterResource(R.drawable.ic_date),
                        headline = stringResource(R.string.text_field_acquisition_date_label),
                        text = motorIdentity.acquisitionDate?.formatMedium() ?: ""
                    )
                }
            } ?: Box(
                Modifier.fillMaxSize()
            ) {
                Text(stringResource(id = R.string.card_machine_identity_not_available_message))
            }
        }
    }
}