package com.jssdvv.ara.schedule.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jssdvv.ara.core.presentation.AppState
import com.jssdvv.ara.machines.presentation.navigation.navigateToActivities
import com.jssdvv.ara.schedule.presentation.destination.events.EventsDestination
import kotlinx.serialization.Serializable

@Serializable
data object EventsGraph {

    @Serializable
    data object EventsRoute
}

fun NavGraphBuilder.eventsNavGraph(
    appState: AppState,
) {
    val navHostController = appState.navHostController
    navigation<EventsGraph>(EventsGraph.EventsRoute) {
        composable<EventsGraph.EventsRoute> {
            EventsDestination(
                onNavigateToActivities = { machineId ->
                    navHostController.navigateToActivities(machineId)
                }
            )
        }
    }
}