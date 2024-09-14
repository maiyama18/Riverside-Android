package com.example.riverside.data.database

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

const val databaseName = "database"

class DatabaseFileClient @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getDatabaseSize(): Long = getDatabaseFile().length()

    private fun getDatabaseFile(): File {
        val databaseFolderPath = context.filesDir.absolutePath.replace("files", "databases")
        val databasePath = "$databaseFolderPath/$databaseName"
        return File(databasePath)
    }
}