package com.example.riverside.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.riverside.ui.components.WithTopBar

@Composable
fun SettingsScreen(navController: NavHostController) {
    WithTopBar(title = "Settings", navController = navController) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Settings")
        }
    }
}
