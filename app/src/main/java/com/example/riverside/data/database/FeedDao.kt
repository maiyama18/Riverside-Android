package com.example.riverside.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(feedEntity: FeedEntity)

    @Query("SELECT * FROM feeds")
    fun findAll(): Flow<List<FeedEntity>>

    @Update
    suspend fun update(feedEntity: FeedEntity)

    @Delete
    suspend fun delete(feedEntity: FeedEntity)
}