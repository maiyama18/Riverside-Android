package com.muijp.riverside.ui.screens.feeds.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muijp.riverside.data.models.Feed
import com.muijp.riverside.data.repositories.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    feedRepository: FeedRepository,
) : ViewModel() {
    val allFeeds: StateFlow<List<Feed>?> = feedRepository.subscribedFeeds()
        .map { feeds -> feeds.sortedByDescending { it.unreadEntryCount } }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}