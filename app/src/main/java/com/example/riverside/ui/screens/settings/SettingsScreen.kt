package com.example.riverside.ui.screens.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.riverside.BuildConfig
import com.example.riverside.ui.components.WithTopBar
import com.example.riverside.ui.navigation.Licenses

@Composable
fun SettingsScreen(
    formattedDatabaseSize: String,
    navController: NavHostController,
) {
    WithTopBar(title = "Settings", navController = navController) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            sectionHeader("Database")
            sectionItem(key = "Storage", value = { Text(formattedDatabaseSize) })

            sectionDivider()

            sectionHeader("About App")
            sectionItem(key = "Version", value = { Text(BuildConfig.VERSION_NAME) })
            sectionChevronItem(key = "Licenses") { navController.navigate(Licenses) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.sectionHeader(title: String) {
    stickyHeader {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

fun LazyListScope.sectionDivider() {
    item {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 0.5.dp,
        )
    }
}

fun LazyListScope.sectionChevronItem(
    key: String,
    onClick: () -> Unit,
) = sectionItem(
    key = key,
    value = {
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    },
    onClick = onClick
)

fun LazyListScope.sectionItem(
    key: String,
    value: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                key,
                style = MaterialTheme.typography.bodyLarge,
            )

            value()
        }
    }
}