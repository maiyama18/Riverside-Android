package com.example.riverside.ui.screens.root

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.riverside.ui.navigation.RootRoute

@Composable
fun RootBottomBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    BottomAppBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.hasRoute(RootRoute.Stream::class) } == true,
            onClick = {
                navController.navigate(RootRoute.Stream) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.RssFeed,
                    contentDescription = "navigate to stream tab"
                )
            },
            label = { Text("Stream") }
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.hasRoute(RootRoute.Feeds::class) } == true,
            onClick = {
                navController.navigate(RootRoute.Feeds) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "navigate to feeds tab"
                )
            },
            label = { Text("Feeds") }
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.hasRoute(RootRoute.Settings::class) } == true,
            onClick = {
                navController.navigate(RootRoute.Settings) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "navigate to settings tab"
                )
            },
            label = { Text("Settings") }
        )
    }
}