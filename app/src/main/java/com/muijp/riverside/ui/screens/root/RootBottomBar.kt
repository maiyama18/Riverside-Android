package com.muijp.riverside.ui.screens.root

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.muijp.riverside.ui.navigation.FeedList
import com.muijp.riverside.ui.navigation.Settings
import com.muijp.riverside.ui.navigation.Stream

@Composable
fun RootBottomBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showsBottomBar = currentDestination?.hasRoute(Stream) == true ||
            currentDestination?.hasRoute(FeedList) == true ||
            currentDestination?.hasRoute(Settings) == true

    if (showsBottomBar) {
        BottomAppBar(modifier = modifier) {
            NavigationBarItem(
                selected = currentDestination?.hasRoute(Stream) == true,
                onClick = {
                    navController.navigate(Stream) {
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
                selected = currentDestination?.hasRoute(FeedList) == true,
                onClick = {
                    navController.navigate(FeedList) {
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
                selected = currentDestination?.hasRoute(Settings) == true,
                onClick = {
                    navController.navigate(Settings) {
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
}