package com.example.riverside.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(
    val url: String,
    val title: String,
    @SerialName("pageURL") val pageUrl: String? = null,
    @SerialName("imageURL") val imageUrl: String? = null,
    val overview: String? = null,
)

@Serializable
data class FeedResultResponse(
    val feed: FeedResponse? = null,
    val error: String? = null,
)

@Serializable
data class FeedsResponse(
    val feeds: Map<String, FeedResultResponse>,
)