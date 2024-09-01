package com.example.riverside.data.models

import io.ktor.http.Url
import kotlinx.datetime.Instant

data class Feed(
    val url: String,
    val title: String,
    val pageUrl: String?,
    val imageUrl: String?,
    val overview: String?,
    val entries: List<Entry>,
) {
    val host: String?
        get() = Url(url).host.ifEmpty { null }

    val unreadEntryCount: Int
        get() = entries.count { !it.read }
}

data class Entry(
    val url: String,
    val title: String,
    val publishedAt: Instant,
    val content: String?,
    var read: Boolean,
)