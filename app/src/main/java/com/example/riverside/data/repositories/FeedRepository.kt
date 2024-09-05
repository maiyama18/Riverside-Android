package com.example.riverside.data.repositories

import com.example.riverside.data.database.EntryDao
import com.example.riverside.data.database.EntryEntity
import com.example.riverside.data.database.FeedDao
import com.example.riverside.data.database.FeedEntity
import com.example.riverside.data.models.Entry
import com.example.riverside.data.models.Feed
import com.example.riverside.data.network.FeedFetcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val feedDao: FeedDao,
    private val entryDao: EntryDao,
    private val feedFetcher: FeedFetcher,
) {
    fun feed(url: String): Flow<Feed?> =
        feedDao.find(url).map {
            it.entries.firstOrNull()?.let { (feedEntity, entryEntities) ->
                feedEntity.toModel(entryEntities)
            }
        }

    fun subscribedFeeds(): Flow<List<Feed>> =
        feedDao.findAll().map { entities ->
            entities.map { (feedEntity, entryEntities) ->
                feedEntity.toModel(entryEntities)
            }
        }

    suspend fun subscribe(feed: Feed) {
        val feedEntity = FeedEntity.fromModel(feed)
        val entryEntities = feed.entries
            .map { EntryEntity.fromModel(it) }
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

    suspend fun updateFeed(url: String, existingFeed: Feed) {
        val feed = feedFetcher.fetchFeed(url, true)

        val feedEntity = FeedEntity.fromModel(feed.toModel())
        val newEntryEntities = feed.entries.map { EntryEntity.fromModel(it.toModel(feed.url)) }
            .filter { entry -> existingFeed.entries.none { it.url == entry.url } }

        feedDao.update(feedEntity)
        feedDao.insert(newEntryEntities)
    }

    fun entries(feedUrl: String): Flow<List<Entry>> =
        entryDao.findAll(feedUrl).map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun updateEntry(entry: Entry) {
        val entryEntity = EntryEntity.fromModel(entry)
        entryDao.update(entryEntity)
    }
}