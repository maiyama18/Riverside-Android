package com.example.riverside.data.repositories

import com.example.riverside.data.database.EntryEntity
import com.example.riverside.data.database.FeedDao
import com.example.riverside.data.database.FeedEntity
import com.example.riverside.data.models.Feed
import com.example.riverside.data.network.FeedFetcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val feedDao: FeedDao,
    private val feedFetcher: FeedFetcher,
) {
    fun subscribedFeeds(): Flow<List<Feed>> =
        feedDao.findAll().map { entities ->
            entities.map { (feedEntity, entryEntities) ->
                feedEntity.toModel(entryEntities)
            }
        }

    suspend fun subscribe(feed: Feed) {
        val feedEntity = FeedEntity.fromModel(feed)
        val entryEntities = feed.entries
            .map { EntryEntity.fromModel(feedEntity.url, it) }
            .sortedByDescending { it.publishedAt }
            .mapIndexed { index, entry ->
                // Mark entries older than the first 3 as read.
                entry.apply {
                    if (index > 2) {
                        read = true
                    }
                }
            }
        feedDao.insert(feedEntity, entryEntities)
    }

    suspend fun fetch(url: String): Feed {
        val feed = feedFetcher.fetchFeed(url, true)
        return feed.toModel()
    }
}