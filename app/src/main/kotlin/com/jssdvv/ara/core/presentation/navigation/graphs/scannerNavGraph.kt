package com.jssdvv.ara.core.presentation.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jssdvv.ara.core.presentation.AraAppState
import com.jssdvv.ara.scanner.presentation.scanner.ScannerScreen
import kotlinx.serialization.Serializable

@Serializable
object ScannerNavGraphDestination

@Serializable
object ScannerDestination

fun NavGraphBuilder.scannerNavGraph(
    appState: AraAppState
) {
    val navHostController = appState.navHostController
    navigation<ScannerNavGraphDestination>(startDestination = ScannerDestination) {
        composable<ScannerDestination> {
            ScannerScreen(
                onNavigateToActivityList = navHostController::navigateToActivitiesList
            )
        }
    }
}
