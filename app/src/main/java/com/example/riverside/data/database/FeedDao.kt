package com.example.riverside.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Transaction
    suspend fun insert(feedEntity: FeedEntity, entryEntities: List<EntryEntity>) {
        insert(feedEntity)
        insert(entryEntities)
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(feedEntity: FeedEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entryEntities: List<EntryEntity>)

    @Query("SELECT * FROM feeds JOIN entries ON feeds.url = entries.feedUrl WHERE feeds.url = :url")
    fun find(url: String): Flow<Map<FeedEntity, List<EntryEntity>>>

    @Query("SELECT * FROM feeds JOIN entries ON feeds.url = entries.feedUrl")
    fun findAll(): Flow<Map<FeedEntity, List<EntryEntity>>>

    @Update
    suspend fun update(feedEntity: FeedEntity)

    @Delete
    suspend fun delete(feedEntity: FeedEntity)
}