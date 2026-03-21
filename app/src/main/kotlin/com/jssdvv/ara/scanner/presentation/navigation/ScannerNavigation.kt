package com.jssdvv.ara.scanner.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jssdvv.ara.core.presentation.AppState
import com.jssdvv.ara.core.presentation.navigation.AppNavGraphItem
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import com.jssdvv.ara.machines.presentation.navigation.machinesNavGraph
import com.jssdvv.ara.scanner.presentation.destination.scanner.ScannerDestination
import kotlinx.serialization.Serializable

@Serializable
data object ScannerGraph {

    @Serializable
    object ScannerRoute
}

/**
 * Scanner navigation graph for QR code scanning and machine identification.
 *
 * Navigation Tree:
 * ```
 * ScannerGraph
 *   └─ ScannerRoute (start)
 *       ↓
 *       MachinesGraph.MachinesRoute
 *       └─ MachinesGraph.SpecsRoute(machineId)
 * ```
 *
 * **Cross-graph navigation:** Scanning a machine QR code navigates first to the machines list,
 * then to the specific machine's specs screen in [machinesNavGraph].
 *
 * @param appState Application state containing the NavHostController for navigation.
 * @see machinesNavGraph
 * @see navigateToSpecs
 */
fun NavGraphBuilder.scannerNavGraph(
    appState: AppState,
) {
    val navHostController = appState.navHostController
    navigation<ScannerGraph>(startDestination = ScannerGraph.ScannerRoute) {
        composable<ScannerGraph.ScannerRoute> {
            ScannerDestination(
                onNavigateToSpecs = { machineId ->
                    navHostController.navigateToSpecs(machineId, appState)
                }
            )
        }
    }
}

internal fun NavController.navigateToSpecs(machineId: Int, appState: AppState) {
    appState.navigateToNavGraphDestination(AppNavGraphItem.MACHINES)
    navigate(MachinesGraph.SpecsRoute(machineId))
}