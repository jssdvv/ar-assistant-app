package com.jssdvv.ara.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.jssdvv.ara.core.presentation.navigation.AppNavGraphItem
import com.jssdvv.ara.core.presentation.navigation.graph.InventoryNavGraph
import com.jssdvv.ara.core.presentation.navigation.graph.MachineryNavGraph
import com.jssdvv.ara.core.presentation.navigation.graph.ScannerNavGraph

@Composable
fun rememberAppState(
    navHostController: NavHostController = rememberNavController(),
): AppState {
    return remember(
        navHostController
    ) {
        AppState(
            navHostController = navHostController,
        )
    }
}

@Stable
class AppState(
    val navHostController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navHostController
            .currentBackStackEntryAsState()
            .value?.destination

    val currentNavGraphDestination: NavDestination?
        @Composable get() = navHostController.currentBackStackEntryAsState()
            .value?.destination?.parent?.findStartDestination()

    val navGraphItems: List<AppNavGraphItem> = AppNavGraphItem.entries

    fun navigateToNavGraphDestination(
        appNavGraphItem: AppNavGraphItem,
    ) {
        val navOptions = navOptions {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when (appNavGraphItem) {
            AppNavGraphItem.SCANNER -> navHostController.navigate(ScannerNavGraph, navOptions)
            AppNavGraphItem.MACHINERY -> navHostController.navigate(MachineryNavGraph, navOptions)
            AppNavGraphItem.INVENTORY -> navHostController.navigate(InventoryNavGraph, navOptions)
        }
    }
}