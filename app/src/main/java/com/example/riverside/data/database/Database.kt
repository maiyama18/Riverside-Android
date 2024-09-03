package com.example.riverside.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FeedEntity::class, EntryEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun entryDao(): EntryDao
}