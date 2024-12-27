package com.jssdvv.ara.core.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jssdvv.ara.core.presentation.AppState
import com.jssdvv.ara.scanner.presentation.ScannerScreen
import kotlinx.serialization.Serializable

// Scanner Nested Navigation Graph Destination
@Serializable object ScannerNavGraph

// Scanner Navigation Graph Destinations
@Serializable object CameraPreviewDestination

fun NavGraphBuilder.scannerNavGraph(
    appState: AppState
) {
    val navHostController = appState.navHostController
    navigation<ScannerNavGraph>(startDestination = CameraPreviewDestination) {
        composable<CameraPreviewDestination> {
            ScannerScreen(
                onNavigateToActivityList = navHostController::navigateToActivitiesList
            )
        }
    }
}
