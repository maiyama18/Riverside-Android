package com.example.riverside.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetClient @Inject constructor() {
    suspend fun updateAllWidgets(context: Context) {
        val manager = GlanceAppWidgetManager(context)
        val widget = UnreadEntriesWidget()
        val glanceIds = manager.getGlanceIds(widget.javaClass)
        glanceIds.forEach { glanceId ->
            widget.update(context, glanceId)
            Log.d("Widget", "Updated widget: $glanceId")
        }
    }
}