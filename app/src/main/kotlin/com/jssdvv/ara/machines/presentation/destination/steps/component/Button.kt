package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.AddIcon
import com.jssdvv.ara.core.presentation.common.EditIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon

@Composable
fun ChangeImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    ButtonWithIcon(
        onClick = onClick,
        modifier = modifier,
        colors = if (isOutlined) ButtonDefaults.outlinedButtonColors() else ButtonDefaults.buttonColors(),
        border = if (isOutlined) ButtonDefaults.outlinedButtonBorder() else null,
        icon = { ChangeIcon() },
        content = { Text(stringResource(R.string.button_image_change_select_action)) }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddStepButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ButtonWithIcon(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonDefaults.MediumContainerHeight),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        icon = { AddIcon() },
        content = {
            Text(
                text = stringResource(R.string.button_editor_add_step_action),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
fun AddOperationButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = ButtonWithIcon(
    onClick = onClick,
    modifier = modifier,
    colors = ButtonDefaults.outlinedButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
    icon = { AddIcon() },
    content = {
        Text(
            text = "Add Operation",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
)

@Composable
fun EditStepButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = ButtonWithIcon(
    onClick = onClick,
    modifier = modifier,
    colors = ButtonDefaults.outlinedButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
    icon = { EditIcon() },
    content = {
        Text(
            text = "Edit Step",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
)

@Composable
fun SquareButton(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = Box(
    modifier = modifier
        .size(68.dp)
        .clickable(onClick = onClick)
        .background(
            shape = MaterialTheme.shapes.small,
            color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
        ),
    contentAlignment = Alignment.Center,
    content = { Text(text) }
)