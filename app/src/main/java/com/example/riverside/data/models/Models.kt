package com.example.riverside.data.models

import io.ktor.http.Url
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

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
    val feedUrl: String,
    var read: Boolean,
) {
    companion object {
        @OptIn(FormatStringsInDatetimeFormats::class)
        val entryDateFormat = LocalDateTime.Format { byUnicodePattern("yyyy/MM/dd") }
    }

    val publishedDateString: String
        get() = publishedAt.toLocalDateTime(TimeZone.currentSystemDefault()).format(entryDateFormat)
}