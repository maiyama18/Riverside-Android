package com.example.riverside.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.riverside.R
import com.example.riverside.data.models.EntryWithFeedInfo
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

    fun sendNewEntriesNotification(entries: List<EntryWithFeedInfo>) {
        if (!notificationManager.areNotificationsEnabled()) return
        if (entries.isEmpty()) return

        val notification = NotificationCompat.Builder(context, NEW_ENTRIES_CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("${entries.size} new entries published")
            .setStyle(
                NotificationCompat.InboxStyle().also {
                    if (entries.size > 6) {
                        entries.take(5).forEach { entry ->
                            it.addLine("${entry.entry.title} | ${entry.feedTitle}")
                        }
                        it.addLine("and more...")
                    } else {
                        entries.take(6).forEach { entry ->
                            it.addLine("${entry.entry.title} | ${entry.feedTitle}")
                        }
                    }
                }
            )
            .setAutoCancel(true)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}