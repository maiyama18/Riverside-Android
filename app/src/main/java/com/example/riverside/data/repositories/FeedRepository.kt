package com.example.riverside.data.repositories

import com.example.riverside.data.database.FeedDao
import com.example.riverside.data.database.FeedEntity
import com.example.riverside.data.network.FeedFetcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val feedDao: FeedDao,
    private val feedFetcher: FeedFetcher,
) {
    fun subscribedFeeds(): Flow<List<FeedEntity>> = feedDao.findAll()
    suspend fun subscribe(feedEntity: FeedEntity) = feedDao.insert(feedEntity)

    suspend fun fetch(url: String) = feedFetcher.fetchFeed(url, true)
}