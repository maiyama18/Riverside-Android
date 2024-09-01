package com.example.riverside.ui.screens.feeds.subscription

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.network.FeedResponse
import com.example.riverside.data.repositories.FeedRepository
import com.example.riverside.ui.controllers.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedSubscriptionScreenState {
    data object Idle : FeedSubscriptionScreenState()
    data object Loading : FeedSubscriptionScreenState()
    data class Success(val feed: FeedResponse) : FeedSubscriptionScreenState()
    data class Error(val message: String) : FeedSubscriptionScreenState()
}

@OptIn(FlowPreview::class)
@HiltViewModel
class FeedSubscriptionViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput

    private val _state =
        MutableStateFlow<FeedSubscriptionScreenState>(FeedSubscriptionScreenState.Idle)
    val state: StateFlow<FeedSubscriptionScreenState> = _state

    private val subscribedFeeds = feedRepository.subscribedFeeds()
    val currentFeedSubscribed: Flow<Boolean> =
        subscribedFeeds.combine(state) { subscribedFeeds, state ->
            if (state is FeedSubscriptionScreenState.Success) {
                subscribedFeeds.any { it.url == state.feed.url }
            } else {
                false
            }
        }

    init {
        viewModelScope.launch {
            urlInput
                .debounce(500)
                .filter { Patterns.WEB_URL.matcher(it).matches() }
                .distinctUntilChanged()
                .collect { fetchFeed(it) }
        }
    }

    fun onUrlInputChange(url: String) {
        _urlInput.value = url
    }

    fun onFeedSubscribe(feed: FeedResponse) {
        viewModelScope.launch {
            try {
                feedRepository.subscribe(feed.toEntity())
                snackbarController.present("Subscribed to ${feed.title}")
                _urlInput.value = ""
            } catch (e: Exception) {
                snackbarController.present("Failed to subscribe to ${feed.title}: ${e.message}")
            }
        }
    }

    private suspend fun fetchFeed(url: String) {
        _state.value = FeedSubscriptionScreenState.Loading
        try {
            val feed = feedRepository.fetch(url)
            _state.value = FeedSubscriptionScreenState.Success(feed)
        } catch (e: Exception) {
            _state.value = FeedSubscriptionScreenState.Error(e.message ?: "unknown error")
        }
    }
}