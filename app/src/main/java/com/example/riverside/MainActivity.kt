package com.example.riverside

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.lifecycleScope
import com.example.riverside.data.notification.NotificationService
import com.example.riverside.data.repositories.FeedRepository
import com.example.riverside.ui.controllers.CustomTabsController
import com.example.riverside.ui.screens.root.RootScreen
import com.example.riverside.ui.theme.DarkTeal
import com.example.riverside.ui.theme.RiversideTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var customTabsController: CustomTabsController

    @Inject
    lateinit var feedRepository: FeedRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(DarkTeal.toArgb()))
        setContent {
            RiversideTheme {
                RootScreen()
            }
        }

        notificationService.createNewEntriesNotificationChannel()

        intent.getStringExtra("entry_url_to_open")?.let { url ->
            customTabsController.launch(this, url)
            lifecycleScope.launch {
                feedRepository.makeEntryAsRead(url)
            }
        }
    }
}
