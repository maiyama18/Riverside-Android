package com.example.riverside.ui.screens.feeds.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = urlInput,
            onValueChange = { viewModel.onUrlInputChanged(it) },
        )

        when (val currentState = state) {
            FeedSubscriptionScreenState.Idle -> {}

            FeedSubscriptionScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is FeedSubscriptionScreenState.Success -> {
                Text(currentState.feed.title)
            }

            is FeedSubscriptionScreenState.Error -> {
                Text(currentState.message)
            }
        }
    }
}
