package com.example.riverside.ui.screens.feeds.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.database.Feed
import com.example.riverside.data.repositories.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    feedRepository: FeedRepository,
) : ViewModel() {
    val allFeeds: StateFlow<List<Feed>> = feedRepository.findAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}