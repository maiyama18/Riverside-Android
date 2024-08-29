package com.example.riverside.data.repositories

import com.example.riverside.data.database.Feed
import com.example.riverside.data.database.FeedDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val feedDao: FeedDao,
) {
    suspend fun subscribe(feed: Feed) = feedDao.insert(feed)
    suspend fun findAll(): Flow<List<Feed>> = feedDao.findAll()
}