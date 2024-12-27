package com.jssdvv.ara.core.presentation.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.jssdvv.ara.core.presentation.AppState
import com.jssdvv.ara.core.presentation.navigation.slideInToLeft
import com.jssdvv.ara.core.presentation.navigation.slideOutToRight
import com.jssdvv.ara.machinery.presentation.ARCameraScreen
import com.jssdvv.ara.machinery.presentation.ActivitiesListScreen
import com.jssdvv.ara.machinery.presentation.MachinesListScreen
import com.jssdvv.ara.machinery.presentation.screen.add_machine.AddMachineScreen
import com.jssdvv.ara.machinery.presentation.screen.edit_activity.EditActivityScreen
import com.jssdvv.ara.machinery.presentation.screen.edit_machine.EditMachineScreen
import kotlinx.serialization.Serializable

// Machinery Nested Navigation Graph Destination
@Serializable object MachineryNavGraph

// Machinery Navigation Graph Destinations
@Serializable object MachineryListDestination
@Serializable object AddMachineDestination
@Serializable data class EditMachineDestination(val machineId: Int)
@Serializable data class ActivitiesListDestination(val machineId: Int)

// Activities Sub-Destinations
@Serializable object AddActivityDestination
@Serializable data class EditActivityDestination(val activityId: Int)
@Serializable data class ARCameraDestination(val activityId: Int)

fun NavGraphBuilder.machineryNavGraph(
    appState: AppState,
) {
    val navHostController = appState.navHostController
    navigation<MachineryNavGraph>(MachineryListDestination) {
        composable<MachineryListDestination> {
            MachinesListScreen(
                onNavigateBack = { navHostController.navigateUp() },
                onNavigateToAddMachine = navHostController::navigateToAddMachine,
                onNavigateToEditMachine = navHostController::navigateToEditMachine,
                onNavigateToActivitiesList = navHostController::navigateToActivitiesList,
            )
        }
        composable<AddMachineDestination>(
            enterTransition = ::slideInToLeft,
            exitTransition = ::slideOutToRight,
            popEnterTransition = ::slideInToLeft,
            popExitTransition = ::slideOutToRight
        ) {
            AddMachineScreen(
                onNavigateBack = { navHostController.navigateUp() }
            )
        }
        composable<EditMachineDestination> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<EditMachineDestination>()
            EditMachineScreen(
                machineId = args.machineId,
                onNavigateBack = { navHostController.navigateUp() }
            )
        }
        composable<ActivitiesListDestination> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<ActivitiesListDestination>()
            ActivitiesListScreen(
                machineId = args.machineId,
                onNavigateBack = { navHostController.navigateUp() },
                onNavigateToAddActivity = navHostController::navigateToAddActivity,
                onNavigateToEditActivity = navHostController::navigateToEditActivity,
                onNavigateToARCamera = navHostController::navigateToARCamera
            )
        }
        composable<AddActivityDestination> {
            //AddActivityScreen()
        }
        composable<EditActivityDestination> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<EditActivityDestination>()
            EditActivityScreen(
                activityId = args.activityId
            )
        }
        composable<ARCameraDestination> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<ARCameraDestination>()
            ARCameraScreen(
                onNavigateBack = {
                    navHostController.navigateUp(
//                        route = ActivitiesListDestination,
//                        inclusive = true
                    )
                }
            )
        }
    }
}

internal fun NavController.navigateToAddMachine() =
    navigate(AddMachineDestination)

internal fun NavController.navigateToEditMachine(machineId: Int) =
    navigate(EditMachineDestination(machineId))

fun NavController.navigateToActivitiesList(machineId: Int) =
    navigate(ActivitiesListDestination(machineId))

internal fun NavController.navigateToAddActivity() =
    navigate(AddActivityDestination)

internal fun NavController.navigateToEditActivity(activityId: Int) =
    navigate(EditActivityDestination(activityId))

internal fun NavController.navigateToARCamera(activityId: Int) =
    navigate(ARCameraDestination(activityId))
