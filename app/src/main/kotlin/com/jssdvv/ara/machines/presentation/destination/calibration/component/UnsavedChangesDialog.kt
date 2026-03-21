package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jssdvv.ara.core.presentation.common.WarningIcon

@Composable
fun UnsavedChangesDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onOpenMarkers: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = { WarningIcon() },
        title = {
            Text(text = "Are you sure you want to leave?")
        },
        text = {
            Column {
                Text("You got markers uncalibrated, if you leave these markers will be disabled for the activities")
            }
        },
        dismissButton = {
            OutlinedButton (
                onClick = onConfirm,
            ) {
                Text(text = "Salir")
            }
        },
        confirmButton = {
            Button (
                onClick = onOpenMarkers,
            ) {
                Text(text = "Abrir Marcadores")
            }
        },
    )
}