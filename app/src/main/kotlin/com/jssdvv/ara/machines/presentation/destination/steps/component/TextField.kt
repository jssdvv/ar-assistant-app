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
import com.jssdvv.ara.core.presentation.common.component.ArrowPreviousItemIcon
import com.jssdvv.ara.machines.domain.model.Tool
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.presentation.component.TranslationMeasurementMenu
import com.jssdvv.ara.machines.presentation.destination.steps.functions.SingleRotationState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.SingleTranslationState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.TimeState


@Composable
fun TranslationTextField(
    name: String,
    state: SingleTranslationState,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) = OutlinedTextField(
    value = state.units,
    onValueChange = {
        state.updateUnits(it)
        onValueChange(it)
    },
    modifier = modifier,
    shape = MaterialTheme.shapes.small,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Decimal,
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
            translation = state.translation,
            onMeasurementChange = { state.updateMeasurement(it) }
        )
    }
)

@Composable
fun PitchTextField(
    name: String,
    state: SingleTranslationState,
    onValueChange: (String) -> Unit,
    isPitch: Boolean,
    modifier: Modifier = Modifier
) = OutlinedTextField(
    value = state.units,
    onValueChange = onValueChange,
    modifier = modifier,
    shape = MaterialTheme.shapes.small,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Decimal,
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
                translation = state.translation,
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
        keyboardType = KeyboardType.Decimal,
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
        id = R.plurals.text_field_entities_count_value,
        selectedItemsCount,
        selectedItemsCount
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

@Composable
fun ToolsTextField(
    currentTool: Tool?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null
) = OutlinedTextField(
    modifier = modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        ),
    value = currentTool?.type?.let { stringResource(it.labelResId) }
        ?: stringResource(R.string.text_field_operation_empty_message), // todo change string
    onValueChange = {},
    label = { Text(stringResource(R.string.text_field_operation_selected_label)) }, // todo change string
    readOnly = true,
    leadingIcon = currentTool?.let {
        {
            Icon(
                painter = painterResource(it.type.iconResId),
                contentDescription = stringResource(it.type.iconContentDescResId)
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

@Composable
fun TimeTextField(
    name: String,
    state: TimeState,
    modifier: Modifier = Modifier,
) = OutlinedTextField(
    value = state.units,
    onValueChange = { state.updateUnits(it) },
    modifier = modifier,
    shape = MaterialTheme.shapes.small,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Decimal,
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
            content = { Text("seconds") } // todo create string
        )
    }
)