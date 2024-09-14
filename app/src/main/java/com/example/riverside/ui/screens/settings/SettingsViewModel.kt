package com.example.riverside.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.example.riverside.data.database.DatabaseFileClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val databaseFileClient: DatabaseFileClient,
) : ViewModel() {
    val formattedDatabaseSize: String
        get() {
            val size = databaseFileClient.getDatabaseSize()
            return when {
                size < 1024 -> "$size B"
                size < 1024 * 1024 -> "${size / 1024} KB"
                size < 1024 * 1024 * 1024 -> "${size / 1024 / 1024} MB"
                else -> "${size / 1024 / 1024 / 1024} GB"
            }
        }
}