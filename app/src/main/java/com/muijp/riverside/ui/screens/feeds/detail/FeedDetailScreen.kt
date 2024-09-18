package com.muijp.riverside.ui.screens.feeds.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.muijp.riverside.BuildConfig
import com.muijp.riverside.data.models.Entry
import com.muijp.riverside.ui.components.ContentUnavailableView
import com.muijp.riverside.ui.components.EntriesFilter
import com.muijp.riverside.ui.components.SwipeAction
import com.muijp.riverside.ui.components.SwipeListItem
import com.muijp.riverside.ui.components.WithTopBar

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
            EntriesFilter(
                selectedFilter = state.filter,
                onFilterSelected = { onEvent(FeedDetailEvent.FilterSelected(it)) },
            )
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
                                onMarkAsUnread = { onEvent(FeedDetailEvent.EntryMarkedAsUnread(it)) },
                                onDelete = { onEvent(FeedDetailEvent.EntryDeleted(it)) },
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = tween(500),
                                        placementSpec = tween(500),
                                        fadeOutSpec = tween(500)
                                    )
                                    .clickable {
                                        onEvent(FeedDetailEvent.EntryClicked(context, entry))
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
    onMarkAsUnread: (Entry) -> Unit,
    onDelete: (Entry) -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeListItem(
        modifier = modifier,
        startAction = if (BuildConfig.DEBUG) SwipeAction(
            icon = Icons.Default.Delete,
            background = MaterialTheme.colorScheme.error,
            action = { onDelete(entry) },
        ) else null,
        endAction = if (entry.read) SwipeAction(
            icon = Icons.AutoMirrored.Filled.Undo,
            background = MaterialTheme.colorScheme.primaryContainer,
            action = { onMarkAsUnread(entry) },
        ) else SwipeAction(
            icon = Icons.Default.Check,
            background = MaterialTheme.colorScheme.primary,
            action = { onMarkAsRead(entry) },
        ),
    ) {
        Column(
            modifier = Modifier
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
