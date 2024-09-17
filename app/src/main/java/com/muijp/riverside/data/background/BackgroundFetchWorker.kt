package com.muijp.riverside.data.background

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.muijp.riverside.data.notification.NotificationService
import com.muijp.riverside.data.repositories.FeedRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackgroundFetchWorker @AssistedInject constructor(
    private val feedRepository: FeedRepository,
    private val notificationService: NotificationService,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    companion object {
        const val NAME = "BackgroundFetchWorker"
    }

    override suspend fun doWork(): Result {
        Log.i("BackgroundFetchWorker", "doWork")
        try {
            val newEntries = feedRepository.updateAllFeeds(force = false)
            Log.i(
                "BackgroundFetchWorker",
                "successfully fetched ${newEntries.size} entries: ${newEntries.joinToString { it.entry.title }}"
            )
            notificationService.sendNewEntriesNotification(newEntries)
            return Result.success()
        } catch (e: Exception) {
            Log.e("BackgroundFetchWorker", "failed to fetch: $e")
            return Result.failure()
        }
    }
}