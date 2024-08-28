package com.example.riverside

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.riverside.ui.screens.root.RootScreen
import com.example.riverside.ui.theme.RiversideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RiversideTheme {
                RootScreen()
            }
        }
    }
}
