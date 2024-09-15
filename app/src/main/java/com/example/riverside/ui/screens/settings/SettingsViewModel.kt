package com.example.riverside.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riverside.data.database.DatabaseFileClient
import com.example.riverside.data.models.Entry
import com.example.riverside.data.notification.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

sealed class SettingsEvent {
    data class NotificationSettingsClicked(val context: Context) : SettingsEvent()

    // DEBUG
    data object DebugNotificationSent : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val databaseFileClient: DatabaseFileClient,
    private val notificationService: NotificationService,
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

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.NotificationSettingsClicked -> viewModelScope.launch {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, event.context.packageName)
                }
                event.context.startActivity(intent)
            }

            SettingsEvent.DebugNotificationSent -> {
                notificationService.sendNewEntriesNotification(
                    listOf(
                        Entry(
                            url = "https://example.com",
                            title = "Debug Notification",
                            publishedAt = Clock.System.now(),
                            content = null,
                            feedUrl = "https://example.com",
                            read = false,
                        ),
                    )
                )
            }
        }
    }
}