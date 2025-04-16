package com.jssdvv.ara.core.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.component.icon.CancelIcon
import com.jssdvv.ara.core.presentation.component.icon.CheckIcon
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    modifier: Modifier = Modifier,
    currentDateSelected: Date? = null,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialMillis = currentDateSelected?.toInstant()?.toEpochMilli()
        ?: LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    val datePickerState: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )
    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault())
                        onDateSelected(Date.from(localDate.toInstant()))
                    } ?: onDateSelected(Date(System.currentTimeMillis()))
                    onDismiss()
                }
            ) {
                CheckIcon()
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.button_check_action)
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
            ) {
                CancelIcon()
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.button_cancel_action)
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
        )
    }
}