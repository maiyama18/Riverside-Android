package com.muijp.riverside.ui.screens.root

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.muijp.riverside.ui.controllers.SnackbarController
import com.muijp.riverside.ui.navigation.RootNavHost

fun NavDestination.hasRoute(route: Any): Boolean = hierarchy.any { it.hasRoute(route::class) }

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    viewModel: RootViewModel = hiltViewModel(),
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

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(RootEvent.Resumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("RootScreen", "Permission granted: $granted")
        }
    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = { RootBottomBar(navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        RootNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}