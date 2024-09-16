package com.example.riverside.ui.screens.stream

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.models.EntriesFilter
import com.example.riverside.data.models.EntryWithFeedInfo
import com.example.riverside.data.models.Feed
import com.example.riverside.data.repositories.FeedRepository
import com.example.riverside.data.repositories.PreferencesRepository
import com.example.riverside.ui.controllers.CustomTabsController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

data class StreamSection(
    val date: LocalDate,
    val entries: List<EntryWithFeedInfo>,
)

data class StreamUiState(
    val feeds: List<Feed>?,
    val filter: EntriesFilter,
    val isRefreshing: Boolean,
) {
    val isNoFeedSubscribed: Boolean
        get() = feeds?.isEmpty() ?: false

    val title: String
        get() = when (val unreadEntryCount = feeds?.sumOf { it.unreadEntryCount }) {
            0, null -> "Stream"
            else -> "Stream ($unreadEntryCount)"
        }

    val sections: List<StreamSection>?
        get() {
            if (feeds == null) {
                return null
            }

            val entries = feeds
                .flatMap { feed ->
                    feed.entries.map { EntryWithFeedInfo(it, feed.url, feed.title, feed.imageUrl) }
                }
                .filter { entry ->
                    when (filter) {
                        EntriesFilter.ALL -> true
                        EntriesFilter.UNREAD -> !entry.entry.read
                    }
                }
                .sortedByDescending { it.entry.publishedAt }

            return entries.groupBy { it.entry.publishedAt.toLocalDateTime(TimeZone.currentSystemDefault()).date }
                .map { (date, entries) -> StreamSection(date, entries) }
                .sortedByDescending { it.date }
        }
}

sealed class StreamEvent {
    data object Resumed : StreamEvent()
    data object PullToRefreshed : StreamEvent()
    data class EntryClicked(val context: Context, val entry: EntryWithFeedInfo) : StreamEvent()
    data class EntryDeleted(val entry: EntryWithFeedInfo) : StreamEvent()
    data class EntryMarkedAsRead(val entry: EntryWithFeedInfo) : StreamEvent()
    data class EntryMarkedAsUnread(val entry: EntryWithFeedInfo) : StreamEvent()
    data class FilterSelected(val filter: EntriesFilter) : StreamEvent()
}

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val preferencesRepository: PreferencesRepository,
    private val customTabsController: CustomTabsController,
) : ViewModel() {
    private val _state: MutableStateFlow<StreamUiState> =
        MutableStateFlow(StreamUiState(null, EntriesFilter.ALL, isRefreshing = false))
    val state: StateFlow<StreamUiState> = _state

    private var openingEntry: EntryWithFeedInfo? = null

    init {
        viewModelScope.launch {
            feedRepository.subscribedFeeds()
                .collect { feeds -> _state.update { it.copy(feeds = feeds) } }
        }
        viewModelScope.launch {
            preferencesRepository.streamEntriesFilter.collect { filter ->
                _state.update { it.copy(filter = filter) }
            }
        }
    }

    fun onEvent(event: StreamEvent) {
        Log.d("StreamViewModel", "onEvent: $event")

        when (event) {
            StreamEvent.Resumed -> openingEntry?.let { entry ->
                viewModelScope.launch(Dispatchers.IO) {
                    feedRepository.updateEntry(entry.entry.copy(read = true))
                    openingEntry = null
                }
            }

            StreamEvent.PullToRefreshed -> viewModelScope.launch {
                _state.update { it.copy(isRefreshing = true) }
                try {
                    feedRepository.updateAllFeeds(force = true)
                } catch (e: Exception) {
                    // TODO: Handle error
                } finally {
                    _state.update { it.copy(isRefreshing = false) }
                }
            }

            is StreamEvent.EntryMarkedAsRead -> viewModelScope.launch {
                feedRepository.updateEntry(event.entry.entry.copy(read = true))
            }

            is StreamEvent.EntryMarkedAsUnread -> viewModelScope.launch {
                feedRepository.updateEntry(event.entry.entry.copy(read = false))
            }

            is StreamEvent.EntryClicked -> {
                openingEntry = event.entry
                customTabsController.launch(event.context, event.entry.entry.url)
            }

            is StreamEvent.EntryDeleted -> viewModelScope.launch {
                feedRepository.deleteEntry(event.entry.entry)
            }

            is StreamEvent.FilterSelected -> viewModelScope.launch {
                _state.update { it.copy(filter = event.filter) }
                preferencesRepository.setStreamEntriesFilter(event.filter)
            }
        }
    }
}