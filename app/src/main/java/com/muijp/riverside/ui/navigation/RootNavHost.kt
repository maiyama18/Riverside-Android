package com.muijp.riverside.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.muijp.riverside.ui.components.WithTopBar
import com.muijp.riverside.ui.screens.feeds.detail.FeedDetailScreen
import com.muijp.riverside.ui.screens.feeds.detail.FeedDetailViewModel
import com.muijp.riverside.ui.screens.feeds.list.FeedListScreen
import com.muijp.riverside.ui.screens.feeds.subscription.FeedSubscriptionScreen
import com.muijp.riverside.ui.screens.settings.SettingsScreen
import com.muijp.riverside.ui.screens.settings.SettingsViewModel
import com.muijp.riverside.ui.screens.stream.StreamScreen
import com.muijp.riverside.ui.screens.stream.StreamViewModel
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
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

@Serializable
object Licenses

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
            val viewModel: StreamViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            StreamScreen(
                state = state,
                onEvent = viewModel::onEvent,
                navController = navController,
            )
        }
        composable<FeedList> {
            FeedListScreen(navController = navController)
        }
        composable<Settings> {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                formattedDatabaseSize = viewModel.formattedDatabaseSize,
                onEvent = viewModel::onEvent,
                navController = navController,
            )
        }

        composable<FeedSubscription> {
            FeedSubscriptionScreen(navController = navController)
        }
        composable<FeedDetail> { backStackEntry ->
            val route: FeedDetail = backStackEntry.toRoute()
            val viewModel: FeedDetailViewModel = hiltViewModel(
                creationCallback = { factory: FeedDetailViewModel.Factory ->
                    factory.create(route.feedUrl)
                }
            )
            val state by viewModel.state.collectAsStateWithLifecycle()
            FeedDetailScreen(
                state = state,
                onEvent = viewModel::onEvent,
                navController = navController
            )
        }
        composable<Licenses> {
            WithTopBar(title = "Licenses", navController = navController) {
                LibrariesContainer(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
