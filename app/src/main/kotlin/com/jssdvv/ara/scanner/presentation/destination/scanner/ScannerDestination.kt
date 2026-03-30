package com.jssdvv.ara.scanner.presentation.destination.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.barcode.common.Barcode
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.PermissionState
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheel
import com.jssdvv.ara.scanner.data.repository.MLKitBarcodeAnalyzer
import com.jssdvv.ara.scanner.presentation.destination.scanner.component.CameraPreview
import com.jssdvv.ara.scanner.presentation.destination.scanner.component.QRSquareCanvas

@Composable
fun ScannerDestination(
    onNavigateToSpecs: (Int) -> Unit,
    viewModel: ScannerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionState by viewModel.cameraPermissionState.collectAsStateWithLifecycle()
    ScannerScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        permissionState = permissionState,
        onNavigateToMachineDetails = onNavigateToSpecs
    )
}

@Composable
fun ScannerScreen(
    modifier: Modifier = Modifier,
    uiState: ScannerUiState,
    permissionState: PermissionState,
    onEvent: (ScannerEvent) -> Unit,
    onNavigateToMachineDetails: (Int) -> Unit,
) {
    val context = LocalContext.current
    val cameraPermission = Manifest.permission.CAMERA

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { onEvent(ScannerEvent.OnCheckPermissionState(cameraPermission)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        val shouldShow =
            (context as Activity).shouldShowRequestPermissionRationale(cameraPermission)
        onEvent(ScannerEvent.OnPermissionInteraction(cameraPermission))
        onEvent(ScannerEvent.OnCheckPermissionState(cameraPermission, shouldShow))
    }

    when (permissionState) {
        PermissionState.Granted -> {
            when (uiState) {
                ScannerUiState.Loading -> {
                    LoadingWheel(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is ScannerUiState.Success -> {
                    ScannerSuccessScreen(
                        isScanning = uiState.isScanning,
                        isTorchEnabled = uiState.isTorchEnabled,
                        onToggleTorchState = { onEvent(ScannerEvent.OnToggleTorch) },
                        onSetTorchState = { onEvent(ScannerEvent.OnSetTorchState(it)) },
                        onNavigateToMachineDetails = onNavigateToMachineDetails
                    )
                }
            }
        }

        is PermissionState.PermanentlyDenied -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(permissionState.rationaleId))
                Button(
                    onClick = {
                        settingsLauncher.launch(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    }
                ) {
                    Text(stringResource(R.string.button_permissions_grant_action))
                }
            }
        }

        is PermissionState.Denied -> {
            LaunchedEffect(true) {
                permissionLauncher.launch(cameraPermission)
            }
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(permissionState.rationaleId))
                Button(
                    onClick = { permissionLauncher.launch(cameraPermission) }
                ) {
                    Text(stringResource(R.string.button_permissions_grant_action))
                }
            }
        }
    }
}

@Composable
fun ScannerSuccessScreen(
    isScanning: Boolean,
    isTorchEnabled: Boolean,
    onToggleTorchState: () -> Unit,
    onSetTorchState: (isTorchEnabled: Boolean) -> Unit,
    onNavigateToMachineDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var detectedQrs by remember { mutableStateOf<List<Barcode>>(emptyList()) }
    var imageWidth by rememberSaveable { mutableStateOf(0) }
    var imageHeight by rememberSaveable { mutableStateOf(0) }
    var previewWidth by rememberSaveable { mutableStateOf(0) }
    var previewHeight by rememberSaveable { mutableStateOf(0) }
    var rotation by rememberSaveable { mutableStateOf(0) }

    DisposableEffect(Unit) { onDispose { onSetTorchState(false) } }

    CameraPreview(
        modifier = modifier,
        analyzer = MLKitBarcodeAnalyzer { barcodes, width, height, rotationDegrees ->
            detectedQrs = barcodes
            imageWidth = width
            imageHeight = height
            rotation = rotationDegrees
        },
        isTorchEnabled = isTorchEnabled,
        onPreviewSizeChanged = { width, height ->
            previewWidth = width
            previewHeight = height
        }
    )

    if (isScanning) {
        detectedQrs.forEach { barcode ->
            QRSquareCanvas(
                barcode = barcode,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                previewWidth = previewWidth,
                previewHeight = previewHeight,
                rotationDegrees = rotation,
                onClick = {
                    onNavigateToMachineDetails(it)
                    onSetTorchState(false)
                }
            )
        }

    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        IconButton(
            onClick = { onToggleTorchState() }
        ) {
            if(isTorchEnabled) {
                Icon(
                    painter = painterResource(R.drawable.ic_torch_filled),
                    tint = Color.Yellow,
                    contentDescription = null
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_torch_outlined),
                    contentDescription = null
                )
            }
        }
    }
}