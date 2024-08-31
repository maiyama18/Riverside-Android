package com.example.riverside.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.riverside.ui.screens.feeds.list.FeedListScreen
import com.example.riverside.ui.screens.feeds.subscription.FeedSubscriptionScreen
import com.example.riverside.ui.screens.settings.SettingsScreen
import com.example.riverside.ui.screens.stream.StreamScreen
import kotlinx.serialization.Serializable

@Serializable
object Stream

@Serializable
object FeedList

@Serializable
object Settings

@Serializable
object FeedSubscription

@Composable
fun RootNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Stream,
        modifier = modifier
    ) {
        composable<Stream> {
            StreamScreen()
        }
        composable<FeedList> {
            FeedListScreen(navController = navController)
        }
        composable<Settings> {
            SettingsScreen()
        }

        composable<FeedSubscription> {
            FeedSubscriptionScreen()
        }
    }
}
