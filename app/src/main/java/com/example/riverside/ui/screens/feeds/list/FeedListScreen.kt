package com.example.riverside.ui.screens.feeds.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.riverside.data.models.Feed
import com.example.riverside.ui.components.ContentUnavailableAction
import com.example.riverside.ui.components.ContentUnavailableView
import com.example.riverside.ui.components.FeedImage
import com.example.riverside.ui.components.WithTopBar
import com.example.riverside.ui.navigation.FeedDetail
import com.example.riverside.ui.navigation.FeedSubscription

@Composable
fun FeedListScreen(
    navController: NavHostController,
    viewModel: FeedListViewModel = hiltViewModel(),
) {
    val feeds by viewModel.allFeeds.collectAsStateWithLifecycle()

    WithTopBar(
        title = "Feeds",
        navController = navController,
        actions = {
            IconButton(onClick = { navController.navigate(FeedSubscription) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        feeds?.let { feeds ->
            if (feeds.isEmpty()) {
                ContentUnavailableView(
                    icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                    title = "No following feed",
                    subtitle = "Subscribe your favorite feeds to see them here",
                    action = ContentUnavailableAction(
                        title = "Subscribe feed",
                        action = { navController.navigate(FeedSubscription) },
                    ),
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(feeds) { index, feed ->
                        FeedListItem(
                            feed = feed,
                            modifier = Modifier.clickable {
                                navController.navigate(FeedDetail(feed.url))
                            },
                        )
                        if (index < feeds.lastIndex) {
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeedListItem(feed: Feed, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeedImage(imageUrl = feed.imageUrl, size = 48.dp)

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(text = feed.title, fontWeight = FontWeight.Bold)

            feed.host?.let { host ->
                Text(
                    host,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }

        if (feed.unreadEntryCount > 0) {
            Text(
                feed.unreadEntryCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}