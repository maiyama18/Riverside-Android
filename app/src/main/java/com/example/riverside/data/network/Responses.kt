package com.example.riverside.data.network

import com.example.riverside.data.models.Feed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(
    val url: String,
    val title: String,
    @SerialName("pageURL") val pageUrl: String? = null,
    @SerialName("imageURL") val imageUrl: String? = null,
    val overview: String? = null,
) {
    fun toModel(): Feed = Feed(
        url = url,
        title = title,
        pageUrl = pageUrl,
        imageUrl = imageUrl,
        overview = overview,
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