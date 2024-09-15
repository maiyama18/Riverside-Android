package com.example.riverside.data.repositories

import com.example.riverside.data.database.EntryDao
import com.example.riverside.data.database.EntryEntity
import com.example.riverside.data.database.FeedDao
import com.example.riverside.data.database.FeedEntity
import com.example.riverside.data.models.Entry
import com.example.riverside.data.models.EntryWithFeedInfo
import com.example.riverside.data.models.Feed
import com.example.riverside.data.network.FeedFetcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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

    suspend fun updateAllFeeds(force: Boolean): List<EntryWithFeedInfo> {
        val existingFeedEntities = feedDao.findAll().firstOrNull()
            ?: if (force) {
                throw IllegalStateException("No feeds to update")
            } else {
                return emptyList()
            }
        val existingFeeds = existingFeedEntities.map { (feedEntity, entryEntities) ->
            feedEntity.toModel(entryEntities)
        }
        val feedsResponse = feedFetcher.fetchFeeds(existingFeeds.map { it.url }, force)
        val fetchedFeeds = feedsResponse.feeds.values.mapNotNull { it.feed?.toModel() }

        val newEntries: MutableList<EntryWithFeedInfo> = mutableListOf()
        existingFeeds.forEach {
            val fetchedFeed = fetchedFeeds.find { fetchedFeed -> fetchedFeed.url == it.url }
            if (fetchedFeed != null) {
                val entries = updateExistingFeed(fetchedFeed, it)
                newEntries += entries.map { entry ->
                    EntryWithFeedInfo(entry, it.url, it.title, it.imageUrl)
                }
            }
        }
        return newEntries
    }

    suspend fun updateFeed(url: String, existingFeed: Feed) {
        val fetchedFeed = feedFetcher.fetchFeed(url, true)
        updateExistingFeed(fetchedFeed.toModel(), existingFeed)
    }

    private suspend fun updateExistingFeed(fetchedFeed: Feed, existingFeed: Feed): List<Entry> {
        val fetchedFeedEntity = FeedEntity.fromModel(fetchedFeed)
        val newEntryEntities = fetchedFeed.entries.map { EntryEntity.fromModel(it) }
            .filter { entry -> existingFeed.entries.none { it.url == entry.url } }

        feedDao.update(fetchedFeedEntity)
        feedDao.insert(newEntryEntities)
        return newEntryEntities.map { it.toModel() }
    }

    fun entries(feedUrl: String): Flow<List<Entry>> =
        entryDao.findAll(feedUrl).map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun updateEntry(entry: Entry) {
        val entryEntity = EntryEntity.fromModel(entry)
        entryDao.update(entryEntity)
    }

    suspend fun deleteEntry(entry: Entry) {
        val entryEntity = EntryEntity.fromModel(entry)
        entryDao.delete(entryEntity)
    }
}