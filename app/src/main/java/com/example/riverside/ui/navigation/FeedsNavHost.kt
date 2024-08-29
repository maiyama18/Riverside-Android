package com.example.riverside.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.riverside.ui.screens.feeds.list.FeedListScreen
import com.example.riverside.ui.screens.feeds.subscription.FeedSubscriptionScreen
import kotlinx.serialization.Serializable

object FeedsRoute {
    @Serializable
    object FeedList

    @Serializable
    object FeedSubscription
}


@Composable
fun FeedsNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = FeedsRoute.FeedList,
        modifier = modifier
    ) {
        composable<FeedsRoute.FeedList> {
            FeedListScreen(navController = navController)
        }
        composable<FeedsRoute.FeedSubscription> {
            FeedSubscriptionScreen()
        }
    }
}