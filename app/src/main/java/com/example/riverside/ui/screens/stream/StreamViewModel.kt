package com.example.riverside.ui.screens.stream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.models.Entry
import com.example.riverside.data.models.Feed
import com.example.riverside.data.repositories.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

data class StreamEntry(
    val entry: Entry,
    val feedUrl: String,
    val feedTitle: String,
    val feedImageUrl: String?,
)

data class StreamSection(
    val date: LocalDate,
    val entries: List<StreamEntry>,
)

data class StreamUiState(
    val feeds: List<Feed>?,
) {
    val sections: List<StreamSection>?
        get() {
            if (feeds == null) {
                return null
            }

            val entries = feeds.flatMap { feed ->
                feed.entries.map { StreamEntry(it, feed.url, feed.title, feed.imageUrl) }
            }.sortedByDescending { it.entry.publishedAt }

            return entries.groupBy { it.entry.publishedAt.toLocalDateTime(TimeZone.currentSystemDefault()).date }
                .map { (date, entries) -> StreamSection(date, entries) }
                .sortedByDescending { it.date }
        }
}

sealed class StreamEvent {
    data class EntryMarkedAsRead(val entry: StreamEntry) : StreamEvent()
    data class EntryMarkedAsUnread(val entry: StreamEntry) : StreamEvent()
    data class EntryDeleted(val entry: StreamEntry) : StreamEvent()
}

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _state: MutableStateFlow<StreamUiState> = MutableStateFlow(StreamUiState(null))
    val state: StateFlow<StreamUiState> = _state

    init {
        viewModelScope.launch {
            feedRepository.subscribedFeeds()
                .collect { feeds -> _state.update { it.copy(feeds = feeds) } }
        }
    }

    fun onEvent(event: StreamEvent) {
        when (event) {
            is StreamEvent.EntryMarkedAsRead -> viewModelScope.launch {
                feedRepository.updateEntry(event.entry.entry.copy(read = true))
            }

            is StreamEvent.EntryMarkedAsUnread -> viewModelScope.launch {
                feedRepository.updateEntry(event.entry.entry.copy(read = false))
            }

            is StreamEvent.EntryDeleted -> viewModelScope.launch {
                feedRepository.deleteEntry(event.entry.entry)
            }
        }
    }
}