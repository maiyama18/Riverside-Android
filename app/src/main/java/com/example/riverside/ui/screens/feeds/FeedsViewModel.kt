package com.example.riverside.ui.screens.feeds

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor() : ViewModel() {
    val title = "Feeds"
}