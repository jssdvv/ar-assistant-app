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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.common.ArrowPreviousItemIcon
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.presentation.component.TranslationMeasurementMenu

enum class Axis(
    val rotationName: String
) {
    X(
        rotationName = "ROLL"
    ),

    Y(
        rotationName = "PITCH"
    ),

    Z(
        rotationName = "YAW"
    )
}

@Composable
fun TranslationTextField(
    axis: Axis,
    value: String,
    onValueChange: (String) -> Unit,
    measurement: Measurement,
    onMeasurementChange: (Measurement) -> Unit,
    modifier: Modifier = Modifier,
) = OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    shape = MaterialTheme.shapes.small,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done
    ),
    leadingIcon = {
        Text(
            text = "${axis.name} = ",
            modifier = Modifier.padding(start = 24.dp)
        )
    },
    trailingIcon = {
        TranslationMeasurementMenu(
            measurement = measurement,
            onMeasurementChange = onMeasurementChange
        )
    }
)

@Composable
fun DistanceTextField(
    name: String,
    value: String,
    onValueChange: (String) -> Unit,
    measurement: Measurement,
    onMeasurementChange: (Measurement) -> Unit,
    modifier: Modifier = Modifier,
) = OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
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
            measurement = measurement,
            onMeasurementChange = onMeasurementChange
        )
    }
)

@Composable
fun RotationTextField(
    axis: Axis,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        leadingIcon = {
            Text(
                text = "${axis.rotationName} = ",
                modifier = Modifier.padding(start = 24.dp)
            )
        },
        trailingIcon = {
            TextButton(
                onClick = {},
                modifier = Modifier.padding(end = 8.dp),
                content = { Text("deg") }
            )
        }
    )
}

@Composable
fun EntitiesTextField(
    selectedItemsCount: Int,
    onScreenChange: (BottomSheetScreen) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null
) = OutlinedTextField(
    modifier = modifier,
    value = when (selectedItemsCount) {
        0 -> "No selected entities"
        1 -> "$selectedItemsCount entity"
        else -> "$selectedItemsCount entities" // todo create strings
    },
    onValueChange = {},
    label = { Text("Selected entities") },
    readOnly = true,
    trailingIcon = {
        IconButton(
            onClick = { onScreenChange(BottomSheetScreen.Entities) },
            content = { ArrowPreviousItemIcon(Modifier.rotate(180F)) }
        )
    },
    interactionSource = interactionSource
)


@Composable
fun OperationsTextField(
    currentOperationType: OperationType?,
    onScreenChange: (BottomSheetScreen) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
) = OutlinedTextField(
    modifier = modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = { onScreenChange(BottomSheetScreen.Operations) }
        ),
    value = currentOperationType?.let { stringResource(it.labelResId) } ?: "No selected operation",  //todo create string
    onValueChange = {},
    label = { Text("Selected operation") }, //todo create string
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
            onClick = { onScreenChange(BottomSheetScreen.Operations) },
            content = { ArrowPreviousItemIcon(Modifier.rotate(180F)) }
        )
    },
    interactionSource = interactionSource
)
