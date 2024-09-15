package com.example.riverside.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.riverside.R
import com.example.riverside.data.models.Entry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    private val notificationManager: NotificationManager,
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val NEW_ENTRIES_CHANNEL_ID = "new_entries"
    }

    fun createNewEntriesNotificationChannel() {
        val channel = NotificationChannel(
            NEW_ENTRIES_CHANNEL_ID,
            "New Entries",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = "Notify when new entries are available." }
        notificationManager.createNotificationChannel(channel)
    }

    fun sendNewEntriesNotification(entries: List<Entry>) {
        if (!notificationManager.areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, NEW_ENTRIES_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("New Entries")
            .setContentText("New entries are available: ${entries.joinToString()}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}