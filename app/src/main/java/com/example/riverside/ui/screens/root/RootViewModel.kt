package com.example.riverside.ui.screens.root

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.repositories.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RootEvent {
    data object Resumed : RootEvent()
}

@HiltViewModel
class RootViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    fun onEvent(event: RootEvent) {
        Log.d("RootViewModel", "onEvent: $event")

        when (event) {
            RootEvent.Resumed -> viewModelScope.launch(Dispatchers.IO) {
                try {
                    feedRepository.updateAllFeeds(force = false)
                } catch (e: Exception) {
                    Log.e("RootViewModel", "Failed to update all feeds", e)
                }
            }
        }
    }
}