package com.example.riverside.ui.screens.feeds.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.models.Feed
import com.example.riverside.data.repositories.FeedRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = FeedDetailViewModel.Factory::class)
class FeedDetailViewModel @AssistedInject constructor(
    @Assisted private val feedUrl: String,
    feedRepository: FeedRepository,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(feedUrl: String): FeedDetailViewModel
    }

    val feed: StateFlow<Feed?> = feedRepository.feed(feedUrl)
        .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = null)
}