package com.jssdvv.ara.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jssdvv.ara.core.presentation.navigation.components.AraNavGraphItems

@Composable
fun rememberAraAppState(
    navHostController: NavHostController = rememberNavController(),
): AraAppState {
    return remember(
        navHostController
    ) {
        AraAppState(
            navHostController = navHostController,
        )
    }
}

@Stable
class AraAppState(
    val navHostController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navHostController
            .currentBackStackEntryAsState()
            .value?.destination

    val currentNavGraphDestination: NavDestination?
        @Composable get() = navHostController.currentBackStackEntryAsState()
            .value?.destination?.parent?.findStartDestination()

    val navGraphItems: List<AraNavGraphItems> = listOf(
        AraNavGraphItems.ScannerNavItem,
        AraNavGraphItems.MachinesNavItem,
        AraNavGraphItems.InventoryNavItem,
        AraNavGraphItems.AgendaNavItem
    )

    fun navigateToGraphDestination(
        graphDestination: Any
    ) {
        navHostController.navigate(graphDestination) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}