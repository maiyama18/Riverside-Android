package com.example.riverside.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries  WHERE feedUrl = :feedUrl")
    fun findAll(feedUrl: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE url = :url")
    suspend fun find(url: String): EntryEntity?

    @Update
    suspend fun update(entryEntity: EntryEntity)

    @Delete
    suspend fun delete(entryEntity: EntryEntity)
}