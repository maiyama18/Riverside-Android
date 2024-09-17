package com.muijp.riverside.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.muijp.riverside.MainActivity
import com.muijp.riverside.R
import com.muijp.riverside.data.models.EntryWithFeedInfo
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
            .setStyle(style(entries))
            .setContentIntent(pendingIntent())
            .setAutoCancel(true)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun style(entries: List<EntryWithFeedInfo>): NotificationCompat.Style {
        return NotificationCompat.InboxStyle().also {
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
    }
}