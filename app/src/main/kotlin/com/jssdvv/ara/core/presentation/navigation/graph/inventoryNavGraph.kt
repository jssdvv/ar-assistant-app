package com.jssdvv.ara.core.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jssdvv.ara.core.presentation.AppState
import com.jssdvv.ara.inventory.presentation.InventoryScreen
import kotlinx.serialization.Serializable

// Inventory Nested Navigation Graph Destination
@Serializable object InventoryNavGraph

// Inventory Navigation Graph Destinations
@Serializable object ItemsListDestination
@Serializable object ItemAddDestination
@Serializable object ItemEditDestination
@Serializable object ItemDetailsDestination

fun NavGraphBuilder.inventoryNavGraph(
    appState: AppState
) {
    val navHostController = appState.navHostController
    navigation<InventoryNavGraph>(startDestination = ItemsListDestination) {
        composable<ItemsListDestination> {
            InventoryScreen(

            )
        }
    }
}
