package com.example.riverside.ui.screens.feeds.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.models.Entry
import com.example.riverside.data.models.Feed
import com.example.riverside.data.repositories.FeedRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FeedDetailUiState(
    val feed: Feed?,
)

sealed class FeedDetailEvent {
    data object Resumed : FeedDetailEvent()
    data class EntryClicked(val entry: Entry) : FeedDetailEvent()
}

@HiltViewModel(assistedFactory = FeedDetailViewModel.Factory::class)
class FeedDetailViewModel @AssistedInject constructor(
    @Assisted private val feedUrl: String,
    private val feedRepository: FeedRepository,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(feedUrl: String): FeedDetailViewModel
    }

    val state: StateFlow<FeedDetailUiState> = feedRepository.feed(feedUrl)
        .map { FeedDetailUiState(it) }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = FeedDetailUiState(null)
        )

    private var openingEntry: Entry? = null

    fun onEvent(event: FeedDetailEvent) {
        when (event) {
            is FeedDetailEvent.EntryClicked -> openingEntry = event.entry
            FeedDetailEvent.Resumed -> {
                openingEntry?.let { entry ->
                    viewModelScope.launch(Dispatchers.IO) {
                        feedRepository.updateEntry(entry.copy(read = true))
                        openingEntry = null
                    }
                }
            }
        }
    }
}