package com.example.riverside.data.repositories

import com.example.riverside.data.database.Feed
import com.example.riverside.data.database.FeedDao
import com.example.riverside.data.network.FeedFetcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val feedDao: FeedDao,
    private val feedFetcher: FeedFetcher,
) {
    fun subscribedFeeds(): Flow<List<Feed>> = feedDao.findAll()
    suspend fun subscribe(feed: Feed) = feedDao.insert(feed)

    suspend fun fetch(url: String) = feedFetcher.fetchFeed(url, true)
}