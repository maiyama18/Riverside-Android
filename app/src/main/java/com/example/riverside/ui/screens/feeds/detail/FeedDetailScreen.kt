package com.example.riverside.ui.screens.feeds.detail

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.riverside.BuildConfig
import com.example.riverside.data.models.EntriesFilter
import com.example.riverside.data.models.Entry
import com.example.riverside.ui.components.ContentUnavailableView
import com.example.riverside.ui.components.WithTopBar

fun launchCustomTabs(context: Context, url: String) {
    CustomTabsIntent.Builder()
        .setUrlBarHidingEnabled(false)
        .build()
        .launchUrl(context, Uri.parse(url))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailScreen(
    state: FeedDetailUiState,
    onEvent: (FeedDetailEvent) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current
    WithTopBar(
        title = state.title,
        navController = navController,
        actions = {
            var filterMenuExpanded by remember { mutableStateOf(false) }
            IconButton(onClick = { filterMenuExpanded = !filterMenuExpanded }) {
                Icon(imageVector = Icons.Default.FilterList, contentDescription = null)
                DropdownMenu(
                    expanded = filterMenuExpanded,
                    onDismissRequest = { filterMenuExpanded = false },
                ) {
                    EntriesFilter.entries.map { filter ->
                        DropdownMenuItem(
                            text = { Text(filter.displayName) },
                            trailingIcon = {
                                if (state.filter == filter) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                    )
                                }
                            },
                            onClick = {
                                onEvent(FeedDetailEvent.FilterSelected(filter))
                                filterMenuExpanded = false
                            },
                        )
                    }
                }
            }
        },
    ) {
        state.feed?.let { feed ->
            val pullToRefreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = state.isRefreshing,
                onRefresh = { onEvent(FeedDetailEvent.PullToRefreshed) },
            ) {
                if (state.visibleEntries.isEmpty()) {
                    ContentUnavailableView(
                        icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                        title = "You've read all entries",
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(
                            state.visibleEntries,
                            key = { _, entry -> entry.url },
                        ) { index, entry ->
                            EntryListItem(
                                entry = entry,
                                onMarkAsRead = { onEvent(FeedDetailEvent.EntryMarkedAsRead(it)) },
                                onDelete = { onEvent(FeedDetailEvent.EntryDeleted(it)) },
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = tween(500),
                                        placementSpec = tween(500),
                                        fadeOutSpec = tween(500)
                                    )
                                    .clickable {
                                        onEvent(FeedDetailEvent.EntryClicked(entry))
                                        launchCustomTabs(context, entry.url)
                                    },
                            )
                            if (index < feed.entries.lastIndex) {
                                HorizontalDivider(thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    onEvent(FeedDetailEvent.Resumed)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}


@Composable
fun EntryListItem(
    entry: Entry,
    onMarkAsRead: (Entry) -> Unit,
    onDelete: (Entry) -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> onDelete(entry)
                SwipeToDismissBoxValue.EndToStart -> onMarkAsRead(entry)
                SwipeToDismissBoxValue.Settled -> {}
            }
            return@rememberSwipeToDismissBoxState it == SwipeToDismissBoxValue.StartToEnd
        }
    )
    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        backgroundContent = {
            val color = when (swipeToDismissBoxState.targetValue) {
                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primary
                SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.error
                SwipeToDismissBoxValue.Settled -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = when (swipeToDismissBoxState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    SwipeToDismissBoxValue.Settled -> Alignment.Center
                }
            ) {
                val icon = when (swipeToDismissBoxState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Icons.Default.Check
                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
                    SwipeToDismissBoxValue.Settled -> null
                }
                icon?.let {
                    Icon(imageVector = it, contentDescription = null, tint = Color.White)
                }
            }
        },
        enableDismissFromEndToStart = !entry.read,
        enableDismissFromStartToEnd = BuildConfig.DEBUG,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            val textColor by animateColorAsState(
                targetValue = MaterialTheme.colorScheme.onSurface.copy(alpha = if (entry.read) 0.4f else 1.0f),
                animationSpec = tween(durationMillis = 500),
                label = "text color",
            )
            Text(
                entry.publishedDateString,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
            )
            Text(
                entry.title,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            entry.content?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
