package com.example.riverside.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.riverside.data.models.Feed

@Entity(tableName = "feeds")
data class FeedEntity(
    @PrimaryKey(autoGenerate = false) val url: String,
    val title: String,
    val pageUrl: String?,
    val imageUrl: String?,
    val overview: String?,
) {
    companion object {
        fun fromModel(feed: Feed): FeedEntity = FeedEntity(
            url = feed.url,
            title = feed.title,
            pageUrl = feed.pageUrl,
            imageUrl = feed.imageUrl,
            overview = feed.overview,
        )
    }

    fun toModel(): Feed = Feed(
        url = url,
        title = title,
        pageUrl = pageUrl,
        imageUrl = imageUrl,
        overview = overview,
    )
}
