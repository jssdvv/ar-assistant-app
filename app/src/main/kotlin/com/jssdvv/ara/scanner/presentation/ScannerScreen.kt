package com.jssdvv.ara.scanner.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.scanner.data.repository.MLKitBarcodeAnalyzer
import com.jssdvv.ara.scanner.presentation.components.CameraPreview
import com.jssdvv.ara.scanner.presentation.components.PermissionDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScannerScreen(
    onNavigateToActivityList: (Int) -> Unit,
) {
    val viewModel = hiltViewModel<ScannerViewModel>()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val permission = Manifest.permission.CAMERA

    when (uiState.value) {
        is ScannerUiState.Loading -> {
            ScannerLoadingScreen(
                modifier = Modifier.fillMaxSize(),
                permission = permission,
                onPermissionGranted = viewModel::onPermissionsGranted,
                onPermissionNeeded = viewModel::onPermissionsNeeded
            )
        }

        is ScannerUiState.RequestingPermission -> {
            ScannerRequestingPermissionsScreen(
                permission = permission,
                isPermissionDialogVisible = (uiState.value as ScannerUiState.RequestingPermission).isPermissionDialogVisible,
                permissionRequestCount = (uiState.value as ScannerUiState.RequestingPermission).permissionRequestCount,
                onPermissionResult = viewModel::onPermissionResult,
                onPermissionGranted = viewModel::onPermissionsGranted,
                setPermissionDialogVisibility = viewModel::setPermissionDialogVisibility
            )
        }

        is ScannerUiState.Success -> {
            ScannerSuccessScreen(
                isScanning = (uiState.value as ScannerUiState.Success).isScanning,
                isTorchEnabled = (uiState.value as ScannerUiState.Success).isTorchEnabled,
                onSetScanningState = viewModel::setScanningState,
                onToggleTorchState = viewModel::toggleTorchState,
                onNavigateToActivityList = onNavigateToActivityList
            )
        }
    }
}

@Composable
fun ScannerLoadingScreen(
    modifier: Modifier = Modifier,
    onPermissionGranted: (Context) -> Unit,
    onPermissionNeeded: (Context) -> Unit,
    context: Context = LocalContext.current,
    permission: String = Manifest.permission.CAMERA,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
    when (ContextCompat.checkSelfPermission(context, permission)) {
        PackageManager.PERMISSION_GRANTED -> onPermissionGranted(context)
        else -> onPermissionNeeded(context)
    }
}

@Composable
fun ScannerRequestingPermissionsScreen(
    modifier: Modifier = Modifier,
    isPermissionDialogVisible: Boolean,
    permissionRequestCount: Int,
    onPermissionResult: (Boolean, Context) -> Unit,
    onPermissionGranted: (Context) -> Unit,
    setPermissionDialogVisibility: (Boolean) -> Unit,
    context: Context = LocalContext.current,
    permission: String = Manifest.permission.CAMERA,
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { onResult ->
        onPermissionResult(onResult, context)
    }

    // TODO: Fix if permissions have been granted and immediately chance ScannerUiState
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        onPermissionGranted(context)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { setPermissionDialogVisibility(true) }
        ) {
            Text(text = "Request Camera Permission")
        }
    }

    when {
        permissionRequestCount < 1 -> {
            LaunchedEffect(true) {
                permissionLauncher.launch(permission)
            }
            if (isPermissionDialogVisible) {
                PermissionDialog(
                    onDismissPermissionDialog = { setPermissionDialogVisibility(false) },
                    onRequestPermission = { permissionLauncher.launch(permission) }
                )
            }
        }

        permissionRequestCount == 1 -> {
            if (isPermissionDialogVisible) {
                PermissionDialog(
                    onDismissPermissionDialog = { setPermissionDialogVisibility(false) },
                    onRequestPermission = { permissionLauncher.launch(permission) }
                )
            }
        }

        // TODO: Fix when permissionRequestCount is greater than 1
        permissionRequestCount > 1 -> {
            if (isPermissionDialogVisible) {
                PermissionDialog(
                    onDismissPermissionDialog = { setPermissionDialogVisibility(false) },
                    onRequestPermission = { permissionLauncher.launch(permission) },
                    isPermanentlyDeclined = true
                )
            }
        }
    }
}

@Composable
fun ScannerSuccessScreen(
    modifier: Modifier = Modifier,
    isScanning: Boolean,
    isTorchEnabled: Boolean,
    onSetScanningState: (Boolean) -> Unit,
    onToggleTorchState: () -> Unit,
    onNavigateToActivityList: (Int) -> Unit,
) {
    var barcodeValue by remember { mutableStateOf<Int?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            // Detiene el escaneo al salir de la pantalla
            onSetScanningState(false)
            // Apaga la linterna si está encendida
            if (isTorchEnabled) {
                onToggleTorchState()
            }
        }
    }

    LaunchedEffect(Unit) {
        barcodeValue = null
    }

    LaunchedEffect(barcodeValue) {
        barcodeValue?.let { value ->
            onNavigateToActivityList(value)
        }
    }

    CameraPreview(
        modifier = Modifier,
        analyzer = MLKitBarcodeAnalyzer { barcode, _, _ ->
            Log.d("mytag", barcode.rawValue.toString())
            barcode.rawValue?.let { barcodeRawValue ->
                barcodeRawValue.toIntOrNull()?.let { integer ->
                    if (barcodeValue != integer) {
                        barcodeValue = integer
                    }
                }
            }
        },
        isTorchEnabled = isTorchEnabled,
    )

    Button(onClick = { onToggleTorchState() }) {

    }
}