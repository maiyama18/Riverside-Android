package com.muijp.riverside.data.network

import com.muijp.riverside.data.models.Entry
import com.muijp.riverside.data.models.Feed
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(
    val url: String,
    val title: String,
    @SerialName("pageURL") val pageUrl: String? = null,
    @SerialName("imageURL") val imageUrl: String? = null,
    val overview: String? = null,
    val entries: List<EntryResponse> = emptyList(),
) {
    fun toModel(): Feed = Feed(
        url = url,
        title = title,
        pageUrl = pageUrl,
        imageUrl = imageUrl,
        overview = overview,
        entries = entries.map { it.toModel(feedUrl = url) },
    )
}

@Serializable
data class EntryResponse(
    val url: String,
    val title: String,
    val publishedAt: String,
    val content: String? = null,
) {
    fun toModel(feedUrl: String): Entry = Entry(
        url = url,
        title = title,
        publishedAt = Instant.parse(publishedAt),
        content = content,
        feedUrl = feedUrl,
        read = false,
    )
}

@Serializable
data class FeedResultResponse(
    val feed: FeedResponse? = null,
    val error: String? = null,
)

@Serializable
data class FeedsResponse(
    val feeds: Map<String, FeedResultResponse>,
)