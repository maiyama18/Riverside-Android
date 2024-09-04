package com.example.riverside.data.models

enum class EntriesFilter {
    ALL,
    UNREAD;

    val displayName: String
        get() = when (this) {
            ALL -> "All"
            UNREAD -> "Unread Only"
        }
}