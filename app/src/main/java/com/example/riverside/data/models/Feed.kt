package com.example.riverside.data.models

import io.ktor.http.Url

data class Feed(
    val url: String,
    val title: String,
    val pageUrl: String? = null,
    val imageUrl: String? = null,
    val overview: String? = null,
) {
    val host: String?
        get() = Url(url).host.ifEmpty { null }
}