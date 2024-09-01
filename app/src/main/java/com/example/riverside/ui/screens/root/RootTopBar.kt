package com.example.riverside.ui.screens.root

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.riverside.ui.navigation.FeedList
import com.example.riverside.ui.navigation.FeedSubscription
import com.example.riverside.ui.navigation.Settings
import com.example.riverside.ui.navigation.Stream
import com.example.riverside.ui.screens.feeds.list.FeedListTopBar
import com.example.riverside.ui.screens.feeds.subscription.FeedSubscriptionTopBar
import com.example.riverside.ui.screens.settings.SettingsTopBar
import com.example.riverside.ui.screens.stream.StreamTopBar

data class TopBarAction(
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiversideTopBar(
    title: String,
    actions: List<TopBarAction> = emptyList(),
) {
    TopAppBar(
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        actions = {
            actions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(imageVector = action.icon, contentDescription = null)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
fun RootTopBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    when {
        currentDestination?.hasRoute(Stream) == true -> StreamTopBar()
        currentDestination?.hasRoute(FeedList) == true -> FeedListTopBar(navController = navController)
        currentDestination?.hasRoute(Settings) == true -> SettingsTopBar()
        currentDestination?.hasRoute(FeedSubscription) == true -> FeedSubscriptionTopBar()

        else -> {}
    }
}
