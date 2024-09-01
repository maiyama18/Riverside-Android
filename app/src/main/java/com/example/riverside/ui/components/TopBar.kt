package com.example.riverside.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.riverside.ui.navigation.FeedList
import com.example.riverside.ui.navigation.Settings
import com.example.riverside.ui.navigation.Stream
import com.example.riverside.ui.screens.root.hasRoute

data class TopBarAction(
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    actions: List<TopBarAction>,
    navController: NavHostController,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val hidesUpButton = currentDestination?.hasRoute(Stream) == true ||
            currentDestination?.hasRoute(FeedList) == true ||
            currentDestination?.hasRoute(Settings) == true ||
            navController.previousBackStackEntry == null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!hidesUpButton) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        actions.forEach { action ->
            IconButton(onClick = action.onClick) {
                Icon(imageVector = action.icon, contentDescription = null)
            }
        }
    }
}

@Composable
fun WithTopBar(
    title: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    actions: List<TopBarAction> = emptyList(),
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopBar(title = title, actions = actions, navController = navController)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}