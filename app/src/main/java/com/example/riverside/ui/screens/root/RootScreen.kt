package com.example.riverside.ui.screens.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.example.riverside.ui.controllers.SnackbarController
import com.example.riverside.ui.navigation.RootNavHost

fun NavDestination.hasRoute(route: Any): Boolean = hierarchy.any { it.hasRoute(route::class) }

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        SnackbarController.snackbarEvents.collect { event ->
            val result = snackbarHostState.showSnackbar(
                event.message,
                duration = event.duration,
                actionLabel = event.action?.label,
                withDismissAction = event.duration == SnackbarDuration.Indefinite,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> event.action?.action?.invoke()
                SnackbarResult.Dismissed -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { RootTopBar(navController = navController) },
        bottomBar = { RootBottomBar(navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        RootNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}