package com.example.riverside.ui.screens.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.riverside.ui.navigation.RootNavHost

@Composable
fun RootScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            RootBottomBar(navController = navController)
        }
    ) { innerPadding ->
        RootNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}