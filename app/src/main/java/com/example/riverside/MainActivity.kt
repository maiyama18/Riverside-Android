package com.example.riverside

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.example.riverside.data.notification.NotificationService
import com.example.riverside.ui.screens.root.RootScreen
import com.example.riverside.ui.theme.DarkTeal
import com.example.riverside.ui.theme.RiversideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var notificationService: NotificationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(DarkTeal.toArgb()))
        setContent {
            RiversideTheme {
                RootScreen()
            }
        }

        notificationService.createNewEntriesNotificationChannel()
    }
}
