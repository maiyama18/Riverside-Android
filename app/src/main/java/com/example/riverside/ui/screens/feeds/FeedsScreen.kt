package com.example.riverside.ui.screens.feeds

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.riverside.ui.components.ContentUnavailableAction
import com.example.riverside.ui.components.ContentUnavailableView

@Composable
fun FeedsScreen(
    viewModel: FeedsViewModel = hiltViewModel(),
) {
    val feeds by viewModel.allFeeds.collectAsStateWithLifecycle()
    if (feeds.isEmpty()) {
        ContentUnavailableView(
            icon = Icons.Default.FormatListBulleted,
            title = "No following feed",
            subtitle = "Follow your favorite feeds to see them here",
            action = ContentUnavailableAction(
                title = "Subscribe feed",
                action = {},
            ),
        )
    } else {
        LazyColumn {
            items(feeds) { feed ->
                Text(text = feed.title)
            }
        }
    }
}
