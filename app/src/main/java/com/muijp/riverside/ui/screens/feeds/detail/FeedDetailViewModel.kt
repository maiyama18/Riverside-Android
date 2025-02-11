package com.muijp.riverside.ui.screens.feeds.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muijp.riverside.data.models.EntriesFilter
import com.muijp.riverside.data.models.Entry
import com.muijp.riverside.data.models.Feed
import com.muijp.riverside.data.repositories.FeedRepository
import com.muijp.riverside.data.repositories.PreferencesRepository
import com.muijp.riverside.ui.controllers.CustomTabsController
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedDetailUiState(
    val feed: Feed?,
    val filter: EntriesFilter,
    val isRefreshing: Boolean,
) {
    val visibleEntries: List<Entry> = feed?.entries?.filter {
        when (filter) {
            EntriesFilter.ALL -> true
            EntriesFilter.UNREAD -> !it.read
        }
    } ?: emptyList()

    val title: String
        get() = feed?.let {
            if (it.unreadEntryCount > 0) {
                "${it.title} (${it.unreadEntryCount})"
            } else {
                it.title
            }
        } ?: ""
}

sealed class FeedDetailEvent {
    data object Resumed : FeedDetailEvent()
    data object PullToRefreshed : FeedDetailEvent()
    data class EntryClicked(val context: Context, val entry: Entry) : FeedDetailEvent()
    data class EntryDeleted(val entry: Entry) : FeedDetailEvent()
    data class EntryMarkedAsRead(val entry: Entry) : FeedDetailEvent()
    data class EntryMarkedAsUnread(val entry: Entry) : FeedDetailEvent()
    data class FilterSelected(val filter: EntriesFilter) : FeedDetailEvent()
}

@HiltViewModel(assistedFactory = FeedDetailViewModel.Factory::class)
class FeedDetailViewModel @AssistedInject constructor(
    @Assisted private val feedUrl: String,
    private val feedRepository: FeedRepository,
    private val preferencesRepository: PreferencesRepository,
    private val customTabsController: CustomTabsController,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(feedUrl: String): FeedDetailViewModel
    }

    private val _state: MutableStateFlow<FeedDetailUiState> =
        MutableStateFlow(FeedDetailUiState(null, EntriesFilter.ALL, false))
    val state: StateFlow<FeedDetailUiState> = _state

    private var openingEntry: Entry? = null

    init {
        viewModelScope.launch {
            feedRepository.feed(feedUrl).collect { feed -> _state.update { it.copy(feed = feed) } }
        }
        viewModelScope.launch {
            preferencesRepository.feedDetailEntriesFilter.collect { filter ->
                _state.update { it.copy(filter = filter) }
            }
        }
    }

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

            FeedDetailEvent.PullToRefreshed -> viewModelScope.launch {
                _state.update { it.copy(isRefreshing = true) }
                try {
                    state.value.feed?.let { feed ->
                        feedRepository.updateFeed(feedUrl, existingFeed = feed)
                    }
                } catch (e: Exception) {
                    // TODO: Handle error
                } finally {
                    _state.update { it.copy(isRefreshing = false) }
                }
            }

            is FeedDetailEvent.EntryClicked -> {
                openingEntry = event.entry
                customTabsController.launch(event.context, event.entry.url)
            }

            is FeedDetailEvent.EntryMarkedAsRead -> viewModelScope.launch {
                feedRepository.updateEntry(event.entry.copy(read = true))
            }

            is FeedDetailEvent.EntryMarkedAsUnread -> viewModelScope.launch {
                feedRepository.updateEntry(event.entry.copy(read = false))
            }

            is FeedDetailEvent.EntryDeleted -> viewModelScope.launch {
                feedRepository.deleteEntry(event.entry)
            }

            is FeedDetailEvent.FilterSelected -> viewModelScope.launch {
                _state.update { it.copy(filter = event.filter) }
                preferencesRepository.setFeedDetailEntriesFilter(event.filter)
            }
        }
    }
}