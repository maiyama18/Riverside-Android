package com.example.riverside.ui.screens.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.riverside.ui.screens.feeds.FeedsScreen
import com.example.riverside.ui.screens.settings.SettingsScreen
import com.example.riverside.ui.screens.stream.StreamScreen
import kotlinx.serialization.Serializable

@Serializable
object Stream

@Serializable
object Feeds

@Serializable
object Settings

@Composable
fun RootNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Stream, modifier = modifier) {
        composable<Stream> {
            StreamScreen()
        }
        composable<Feeds> {
            FeedsScreen()
        }
        composable<Settings> {
            SettingsScreen()
        }
    }
}
