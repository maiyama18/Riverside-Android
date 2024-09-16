package com.example.riverside.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.riverside.data.models.EntriesFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        val STREAM_ENTRIES_FILTER = stringPreferencesKey("stream-entries-filter")
        val FEED_DETAIL_ENTRIES_FILTER = stringPreferencesKey("feed-detail-entries-filter")
    }

    val streamEntriesFilter: Flow<EntriesFilter> = dataStore.data.map { preferences ->
        preferences[STREAM_ENTRIES_FILTER]?.let { EntriesFilter.valueOf(it) } ?: EntriesFilter.ALL
    }

    suspend fun setStreamEntriesFilter(entriesFilter: EntriesFilter) {
        dataStore.edit { preferences -> preferences[STREAM_ENTRIES_FILTER] = entriesFilter.name }
    }

    val feedDetailEntriesFilter: Flow<EntriesFilter> = dataStore.data.map { preferences ->
        preferences[FEED_DETAIL_ENTRIES_FILTER]?.let { EntriesFilter.valueOf(it) }
            ?: EntriesFilter.ALL
    }

    suspend fun setFeedDetailEntriesFilter(entriesFilter: EntriesFilter) {
        dataStore.edit { preferences ->
            preferences[FEED_DETAIL_ENTRIES_FILTER] = entriesFilter.name
        }
    }
}