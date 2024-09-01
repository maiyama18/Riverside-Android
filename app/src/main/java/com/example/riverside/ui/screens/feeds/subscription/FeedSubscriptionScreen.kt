package com.example.riverside.ui.screens.feeds.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.riverside.ui.screens.root.RiversideTopBar

@Composable
fun FeedSubscriptionTopBar() {
    RiversideTopBar(title = "Feed Subscription")
}

@Composable
fun FeedSubscriptionScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedSubscriptionViewModel = hiltViewModel(),
) {
    val urlInput by viewModel.urlInput.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            value = urlInput,
            onValueChange = { viewModel.onUrlInputChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Blog/Feed URL") },
            placeholder = { Text("https://example.com/feed") },
        )

        when (val currentState = state) {
            FeedSubscriptionScreenState.Idle -> {}

            FeedSubscriptionScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is FeedSubscriptionScreenState.Success -> {
                FeedSummaryView(
                    feed = currentState.feed,
                    feedAlreadySubscribed = false, // FIXME
                    onSubscribeClick = { viewModel.onFeedSubscribe(currentState.feed) },
                )
            }

            is FeedSubscriptionScreenState.Error -> {
                FeedErrorView(errorMessage = currentState.message)
            }
        }
    }
}
