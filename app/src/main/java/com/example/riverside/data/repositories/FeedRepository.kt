package com.example.riverside.data.repositories

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
        feedDao.findAll().map { entities -> entities.map { it.toModel() } }

    suspend fun subscribe(feed: Feed) {
        val feedEntity = FeedEntity.fromModel(feed)
        feedDao.insert(feedEntity)
    }

    suspend fun fetch(url: String): Feed {
        val feed = feedFetcher.fetchFeed(url, true)
        return feed.toModel()
    }
}