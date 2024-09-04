package com.example.riverside.ui.screens.feeds.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.models.EntriesFilter
import com.example.riverside.data.models.Entry
import com.example.riverside.data.models.Feed
import com.example.riverside.data.repositories.FeedRepository
import com.example.riverside.data.repositories.PreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FeedDetailUiState(
    val feed: Feed?,
    val filter: EntriesFilter,
) {
    val visibleEntries: List<Entry> = feed?.entries?.filter {
        when (filter) {
            EntriesFilter.ALL -> true
            EntriesFilter.UNREAD -> !it.read
        }
    } ?: emptyList()
}

sealed class FeedDetailEvent {
    data object Resumed : FeedDetailEvent()
    data class EntryClicked(val entry: Entry) : FeedDetailEvent()
    data class FilterSelected(val filter: EntriesFilter) : FeedDetailEvent()
}

@HiltViewModel(assistedFactory = FeedDetailViewModel.Factory::class)
class FeedDetailViewModel @AssistedInject constructor(
    @Assisted private val feedUrl: String,
    private val feedRepository: FeedRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(feedUrl: String): FeedDetailViewModel
    }

    val state: StateFlow<FeedDetailUiState> = feedRepository.feed(feedUrl)
        .combine(preferencesRepository.feedDetailEntriesFilter) { feed, filter ->
            FeedDetailUiState(feed, filter)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = FeedDetailUiState(null, EntriesFilter.ALL)
        )

    private var openingEntry: Entry? = null

    fun onEvent(event: FeedDetailEvent) {
        when (event) {
            FeedDetailEvent.Resumed -> {
                openingEntry?.let { entry ->
                    viewModelScope.launch(Dispatchers.IO) {
                        feedRepository.updateEntry(entry.copy(read = true))
                        openingEntry = null
                    }
                }
            }

            is FeedDetailEvent.EntryClicked -> openingEntry = event.entry
            is FeedDetailEvent.FilterSelected -> viewModelScope.launch {
                preferencesRepository.setFeedDetailEntriesFilter(event.filter)
            }
        }
    }
}