package com.jssdvv.ara.core.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import com.jssdvv.ara.core.presentation.AppState
import com.jssdvv.ara.core.presentation.navigation.graph.ScannerNavGraph
import com.jssdvv.ara.core.presentation.navigation.graph.scannerNavGraph
import com.jssdvv.ara.core.presentation.navigation.graph.inventoryNavGraph
import com.jssdvv.ara.core.presentation.navigation.graph.machineryNavGraph

@Composable
fun AppNavHost(
    appState: AppState,
    modifier: Modifier,
) {
    val navHostController = appState.navHostController
    NavHost(
        startDestination = ScannerNavGraph,
        navController = navHostController,
        modifier = modifier
    ) {
        scannerNavGraph(appState)
        machineryNavGraph(appState)
        inventoryNavGraph(appState)
    }
}

fun slideInToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    )
}

fun slideInToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    )
}

fun slideOutToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    )
}

fun slideOutToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    )
}
