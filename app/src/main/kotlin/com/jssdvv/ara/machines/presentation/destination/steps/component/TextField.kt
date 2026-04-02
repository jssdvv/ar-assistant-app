package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.ArrowPreviousItemIcon
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.presentation.component.TranslationMeasurementMenu
import com.jssdvv.ara.machines.domain.utility.SingleRotationState
import com.jssdvv.ara.machines.domain.utility.SingleTranslationState


@Composable
fun TranslationTextField(
    name: String,
    state: SingleTranslationState,
    modifier: Modifier = Modifier,
) = OutlinedTextField(
    value = state.units,
    onValueChange = { state.updateUnits(it) },
    modifier = modifier,
    shape = MaterialTheme.shapes.small,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done
    ),
    leadingIcon = {
        Text(
            text = "$name = ",
            modifier = Modifier.padding(start = 24.dp)
        )
    },
    trailingIcon = {
        TranslationMeasurementMenu(
            measurement = state.measurement,
            onMeasurementChange = { state.updateMeasurement(it) }
        )
    }
)

@Composable
fun PitchTextField(
    name: String,
    state: SingleTranslationState,
    isPitch: Boolean,
    modifier: Modifier = Modifier
) = OutlinedTextField(
    value = state.units,
    onValueChange = { state.updateUnits(it) },
    modifier = modifier,
    shape = MaterialTheme.shapes.small,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done
    ),
    leadingIcon = {
        Text(
            text = "$name = ",
            modifier = Modifier.padding(start = 24.dp)
        )
    },
    trailingIcon = if (isPitch) {
        {
            TranslationMeasurementMenu(
                measurement = state.measurement,
                onMeasurementChange = { state.updateMeasurement(it) }
            )
        }
    } else null
)

@Composable
fun RotationTextField(
    name: String,
    state: SingleRotationState,
    modifier: Modifier = Modifier,
) = OutlinedTextField(
    value = state.units,
    onValueChange = { state.updateUnits(it) },
    modifier = modifier,
    shape = MaterialTheme.shapes.small,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done
    ),
    leadingIcon = {
        Text(
            text = "$name = ",
            modifier = Modifier.padding(start = 24.dp)
        )
    },
    trailingIcon = {
        TextButton(
            onClick = {},
            modifier = Modifier.padding(end = 8.dp),
            content = { Text("deg") } // todo create string
        )
    }
)

@Composable
fun EntitiesTextField(
    selectedItemsCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null
) = OutlinedTextField(
    modifier = modifier,
    value = pluralStringResource(
        count = selectedItemsCount,
        id = R.plurals.text_field_entities_count_value,
    ),
    onValueChange = {},
    label = { Text(stringResource(R.string.text_field_entities_selected_label)) },
    readOnly = true,
    trailingIcon = {
        IconButton(
            onClick = onClick,
            content = { ArrowPreviousItemIcon(Modifier.rotate(180F)) }
        )
    },
    interactionSource = interactionSource
)

@Composable
fun OperationsTextField(
    currentOperationType: OperationType?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
) = OutlinedTextField(
    modifier = modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        ),
    value = currentOperationType?.let { stringResource(it.labelResId) }
        ?: stringResource(R.string.text_field_operation_empty_message),
    onValueChange = {},
    label = { Text(stringResource(R.string.text_field_operation_selected_label)) },
    readOnly = true,
    leadingIcon = currentOperationType?.let {
        {
            Icon(
                painter = painterResource(it.iconResId),
                contentDescription = stringResource(it.iconContentDescResId)
            )
        }
    },
    trailingIcon = {
        IconButton(
            onClick = onClick,
            content = { ArrowPreviousItemIcon(Modifier.rotate(180F)) }
        )
    },
    interactionSource = interactionSource
)