package com.jssdvv.ara.core.presentation.common.component

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.PermissionState
import com.jssdvv.ara.core.presentation.common.state.Permission
import com.jssdvv.ara.core.presentation.theme.spacing

@Composable
fun PermanentlyDeniedPermissions(
    permanentlyDeniedRationales: Set<Int>,
    settingsLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.small,
            Alignment.CenterVertically
        )
    ) {
        permanentlyDeniedRationales.forEach {
            Text(stringResource(it))
        }

        Button(
            onClick = {
                settingsLauncher.launch(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )
            },
            content = { Text(stringResource(R.string.button_permissions_grant_action)) }
        )
    }
}

@Composable
fun DeniedPermissions(
    deniedPermissions: Set<Permission>,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    modifier: Modifier = Modifier
) {
    val permissionsToRequest = deniedPermissions.map { it.manifestString }.toTypedArray()

    LaunchedEffect(true) { permissionLauncher.launch(permissionsToRequest) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.small,
            Alignment.CenterVertically
        )
    ) {
        deniedPermissions.forEach {
            Text(stringResource((it.state as PermissionState.Denied).rationaleId))
        }

        Button(
            onClick = { permissionLauncher.launch(permissionsToRequest) },
            content = { Text(stringResource(R.string.button_permissions_request_again_action)) }
        )
    }
}