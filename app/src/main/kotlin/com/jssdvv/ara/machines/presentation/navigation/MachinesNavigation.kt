package com.jssdvv.ara.machines.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.jssdvv.ara.core.presentation.AppState
import com.jssdvv.ara.machines.presentation.destination.activities.ActivitiesDestination
import com.jssdvv.ara.machines.presentation.destination.ar_session.ARSessionDestination
import com.jssdvv.ara.machines.presentation.destination.calibration.CalibrationDestination
import com.jssdvv.ara.machines.presentation.destination.documents.DocumentsDestination
import com.jssdvv.ara.machines.presentation.destination.machines.MachinesDestination
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersDestination
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsDestination
import com.jssdvv.ara.machines.presentation.destination.steps.StepsDestination
import kotlinx.serialization.Serializable

@Serializable
data object MachinesGraph {

    @Serializable
    data object MachinesRoute

    @Serializable
    data class SpecsRoute(val machineId: Int)

    @Serializable
    data class ActivitiesRoute(val machineId: Int)

    @Serializable
    data class DocumentsRoute(val machineId: Int)

    @Serializable
    data class StepsRoute(val machineId: Int, val activityId: Int)

    @Serializable
    data class MarkersRoute(val machineId: Int)

    @Serializable
    data class CalibrationRoute(val machineId: Int)

    @Serializable
    data class ARSessionRoute(val machineId: Int, val activityId: Int)
}

const val baseUri = "https://arassistant.vercel.app"

/**
 * Machines navigation graph containing all machine-related destinations.
 *
 * Navigation Tree:
 * ```
 * MachinesGraph
 *   └─ MachinesRoute
 *       └─ SpecsRoute(machineId)
 *           ├─ DocumentsRoute(machineId)
 *           └─ ActivitiesRoute(machineId)
 *               ├─ AnimationsRoute(machineId, activityId)
 *               ├─ CalibrationRoute(machineId)
 *               ├─ MarkersRoute(machineId)
 *               └─ ARSessionRoute(activityId)
 * ```
 * **Note:** Add operations (AddMachine, AddActivity, AddMarker, AddAnimation) are handled via
 * dialogs within their respective screens to avoid unnecessary navigation overhead.
 *
 * @param appState Application state containing the NavHostController for navigation.
 */
fun NavGraphBuilder.machinesNavGraph(
    appState: AppState,
) {
    val navHostController = appState.navHostController
    navigation<MachinesGraph>(MachinesGraph.MachinesRoute) {
        composable<MachinesGraph.MachinesRoute> {
            MachinesDestination(
                onNavigateToSpecs = navHostController::navigateToSpecs,
            )
        }
        composable<MachinesGraph.SpecsRoute>(
            deepLinks = listOf(navDeepLink<MachinesGraph.SpecsRoute>(basePath = "${baseUri}/machine"))
        ) {
            MachineDetailsDestination(
                onNavigateBack = { navHostController.navigateUp() },
                onNavigateToActivities = navHostController::navigateToActivities,
                onNavigateToDocuments = navHostController::navigateToDocuments
            )
        }
        composable<MachinesGraph.ActivitiesRoute> {
            ActivitiesDestination(
                onNavigateBack = { navHostController.navigateUp() },
                onNavigateToMarkers = navHostController::navigateToMarkers,
                onNavigateToCalibration = navHostController::navigateToCalibration,
                onNavigateToARSession = navHostController::navigateToARSession,
                onNavigateToAnimations = navHostController::navigateToSteps
            )
        }
        composable<MachinesGraph.DocumentsRoute> {
            DocumentsDestination(
                onNavigateBack = { navHostController.navigateUp() }
            )
        }
        composable<MachinesGraph.StepsRoute> {
            StepsDestination(
                onNavigateUp = { navHostController.navigateUp() }
            )
        }
        composable<MachinesGraph.MarkersRoute> {
            MarkersDestination(
                onNavigateBack = { navHostController.navigateUp() },
            )
        }
        composable<MachinesGraph.CalibrationRoute> {
            CalibrationDestination(
                onNavigateBack = { navHostController.navigateUp() },
                onNavigateToMarkers = navHostController::navigateToMarkers
            )
        }
        composable<MachinesGraph.ARSessionRoute> {
            ARSessionDestination(
                onNavigateBack = { navHostController.navigateUp() }
            )
        }
    }
}

internal fun NavController.navigateToSpecs(machineId: Int) =
    navigate(MachinesGraph.SpecsRoute(machineId))

fun NavController.navigateToActivities(machineId: Int) =
    navigate(MachinesGraph.ActivitiesRoute(machineId))

internal fun NavController.navigateToDocuments(machineId: Int) =
    navigate(MachinesGraph.DocumentsRoute(machineId))

internal fun NavController.navigateToSteps(machineId: Int, activityId: Int) =
    navigate(MachinesGraph.StepsRoute(machineId, activityId))

internal fun NavController.navigateToMarkers(machineId: Int) =
    navigate(MachinesGraph.MarkersRoute(machineId))

internal fun NavController.navigateToCalibration(machineId: Int) =
    navigate(MachinesGraph.CalibrationRoute(machineId))

internal fun NavController.navigateToARSession(machineId: Int, activityId: Int) =
    navigate(MachinesGraph.ARSessionRoute(machineId, activityId))