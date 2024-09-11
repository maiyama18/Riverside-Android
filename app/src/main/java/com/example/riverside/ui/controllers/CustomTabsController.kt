package com.example.riverside.ui.controllers

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import javax.inject.Inject

class CustomTabsController @Inject constructor() {
    fun launch(context: Context, url: String) {
        CustomTabsIntent.Builder()
            .setUrlBarHidingEnabled(false)
            .build()
            .launchUrl(context, Uri.parse(url))
    }
}