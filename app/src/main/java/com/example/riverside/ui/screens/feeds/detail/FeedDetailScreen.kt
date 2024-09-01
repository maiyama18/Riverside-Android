package com.example.riverside.ui.screens.feeds.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.riverside.data.models.Entry
import com.example.riverside.ui.components.WithTopBar

@Composable
fun FeedDetailScreen(
    feedUrl: String,
    navController: NavHostController,
    viewModel: FeedDetailViewModel = hiltViewModel(
        creationCallback = { factory: FeedDetailViewModel.Factory ->
            factory.create(feedUrl)
        }
    ),
) {
    val feed by viewModel.feed.collectAsStateWithLifecycle()

    WithTopBar(title = feed?.title ?: "", navController = navController) {
        feed?.let { currentFeed ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(currentFeed.entries) { index, entry ->
                    EntryListItem(entry = entry)
                    if (index < currentFeed.entries.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }
}


@Composable
fun EntryListItem(entry: Entry, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            entry.publishedDateString,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            entry.title,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        entry.content?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
