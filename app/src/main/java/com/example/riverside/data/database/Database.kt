package com.example.riverside.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Feed::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun feedDao(): FeedDao
}