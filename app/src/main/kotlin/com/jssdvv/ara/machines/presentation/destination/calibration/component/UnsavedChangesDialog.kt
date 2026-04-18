package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.WarningIcon

@Composable
fun UnsavedChangesDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onOpenModels: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = { WarningIcon() },
        title = { Text(stringResource(R.string.calibration_dialog_unsaved_changes_title)) },
        text = { Text(stringResource(R.string.calibration_dialog_unsaved_changes_warning)) },
        dismissButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                content = { Text(stringResource(R.string.calibration_dialog_unsaved_changes_dismiss_action)) }
            )
        },
        confirmButton = {
            Button(
                onClick = onOpenModels,
                content = { Text(stringResource(R.string.calibration_dialog_unsaved_changes_open_models_action)) }
            )
        },
    )
}