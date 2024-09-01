package com.example.riverside.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.riverside.ui.screens.feeds.detail.FeedDetailScreen
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

@Serializable
data class FeedDetail(val feedUrl: String)

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
            StreamScreen(navController = navController)
        }
        composable<FeedList> {
            FeedListScreen(navController = navController)
        }
        composable<Settings> {
            SettingsScreen(navController = navController)
        }

        composable<FeedSubscription> {
            FeedSubscriptionScreen(navController = navController)
        }
        composable<FeedDetail> { backStackEntry ->
            val route: FeedDetail = backStackEntry.toRoute()
            FeedDetailScreen(feedUrl = route.feedUrl, navController = navController)
        }
    }
}
