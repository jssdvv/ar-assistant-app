package com.jssdvv.ara.schedule.presentation.navigation

import EventsDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jssdvv.ara.core.presentation.AppState
import kotlinx.serialization.Serializable

// Schedule Graph Route
@Serializable
data object ScheduleGraph {

    @Serializable
    data object EventsRoute
}

/**
 * Schedule navigation graph containing all scheduling and event-related destinations.
 *
 * Navigation Tree:
 * ```
 * ScheduleGraph
 *   └─ EventsRoute
 * ```
 *
 * **Note:** Add/Edit event operations are handled via dialogs within the EventsRoute
 * screen to avoid unnecessary navigation overhead.
 *
 * @param appState Application state containing the NavHostController for navigation.
 */
fun NavGraphBuilder.scheduleNavGraph(
    appState: AppState,
) {
    val navHostController = appState.navHostController
    navigation<ScheduleGraph>(ScheduleGraph.EventsRoute) {
        composable<ScheduleGraph.EventsRoute> {
            EventsDestination()
        }
    }
}