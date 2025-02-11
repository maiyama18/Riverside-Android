package com.muijp.riverside.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.muijp.riverside.data.models.Entry
import com.muijp.riverside.data.models.Feed
import kotlinx.datetime.Instant

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

    fun toModel(entryEntities: List<EntryEntity>): Feed = Feed(
        url = url,
        title = title,
        pageUrl = pageUrl,
        imageUrl = imageUrl,
        overview = overview,
        entries = entryEntities.map { it.toModel() },
    )
}

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey(autoGenerate = false) val url: String,
    val feedUrl: String,
    val title: String,
    val publishedAt: String,
    val content: String?,
    var read: Boolean = false,
) {
    companion object {
        fun fromModel(entry: Entry): EntryEntity = EntryEntity(
            url = entry.url,
            feedUrl = entry.feedUrl,
            title = entry.title,
            publishedAt = entry.publishedAt.toString(),
            content = entry.content,
            read = entry.read,
        )
    }

    fun toModel(): Entry = Entry(
        url = url,
        title = title,
        publishedAt = Instant.parse(publishedAt),
        content = content,
        feedUrl = feedUrl,
        read = read,
    )
}
