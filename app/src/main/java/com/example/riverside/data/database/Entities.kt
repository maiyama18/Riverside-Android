package com.example.riverside.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feeds")
data class Feed(
    @PrimaryKey(autoGenerate = false) val url: String,
    val title: String,
    val pageUrl: String?,
    val imageUrl: String?,
    val overview: String?,
)
