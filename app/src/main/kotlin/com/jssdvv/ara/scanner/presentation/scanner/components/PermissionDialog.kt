package com.jssdvv.ara.scanner.presentation.scanner.components

import android.provider.Settings
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    onDismissPermissionDialog: () -> Unit,
    onRequestPermission: () -> Unit,
    isPermanentlyDeclined: Boolean = false
) {
    val context = LocalContext.current

    // TODO: Add some description and pretty UI
    AlertDialog(
        onDismissRequest = onDismissPermissionDialog,
        confirmButton = {
            Button(
                onClick = {
                    if (!isPermanentlyDeclined){
                        onDismissPermissionDialog()
                        onRequestPermission()
                    } else {
                        onDismissPermissionDialog()
                        context.startActivity()
                    }
                }
            ) {
                Text(
                    text = if (!isPermanentlyDeclined) "Confirm" else "Open Config"
                )
            }
        }
    )
}

private fun Context.startActivity() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}