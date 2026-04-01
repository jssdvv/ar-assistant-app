package com.jssdvv.ara.schedule.presentation.navigation

import ToolsDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jssdvv.ara.core.presentation.AppState
import kotlinx.serialization.Serializable

// Schedule Graph Route
@Serializable
data object ToolsGraph {

    @Serializable
    data object ToolsRoute
}

/**
 * Schedule navigation graph containing all scheduling and event-related destinations.
 *
 * Navigation Tree:
 * ```
 * ScheduleGraph
 *   └─ ToolsRoute
 * ```
 *
 * @param appState Application state containing the NavHostController for navigation.
 */
fun NavGraphBuilder.toolsNavGraph(
    appState: AppState,
) {
    val navHostController = appState.navHostController
    navigation<ToolsGraph>(ToolsGraph.ToolsRoute) {
        composable<ToolsGraph.ToolsRoute> {
            ToolsDestination()
        }
    }
}