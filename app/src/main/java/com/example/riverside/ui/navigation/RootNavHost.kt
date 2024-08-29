package com.example.riverside.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.riverside.ui.screens.settings.SettingsScreen
import com.example.riverside.ui.screens.stream.StreamScreen
import kotlinx.serialization.Serializable

object RootRoute {
    @Serializable
    object Stream

    @Serializable
    object Feeds

    @Serializable
    object Settings
}

@Composable
fun RootNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val feedsNavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RootRoute.Stream,
        modifier = modifier
    ) {
        composable<RootRoute.Stream> {
            StreamScreen()
        }
        composable<RootRoute.Feeds> {
            FeedsNavHost(navController = feedsNavHostController)
        }
        composable<RootRoute.Settings> {
            SettingsScreen()
        }
    }
}
