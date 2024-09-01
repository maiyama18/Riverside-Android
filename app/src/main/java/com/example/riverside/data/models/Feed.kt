package com.example.riverside.data.models

data class Feed(
    val url: String,
    val title: String,
    val pageUrl: String? = null,
    val imageUrl: String? = null,
    val overview: String? = null,
)