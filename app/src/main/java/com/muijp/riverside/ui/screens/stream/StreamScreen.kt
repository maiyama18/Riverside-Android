package com.muijp.riverside.ui.screens.stream

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.muijp.riverside.BuildConfig
import com.muijp.riverside.data.models.EntryWithFeedInfo
import com.muijp.riverside.ui.components.ContentUnavailableAction
import com.muijp.riverside.ui.components.ContentUnavailableView
import com.muijp.riverside.ui.components.EntriesFilter
import com.muijp.riverside.ui.components.FeedImage
import com.muijp.riverside.ui.components.SwipeAction
import com.muijp.riverside.ui.components.SwipeListItem
import com.muijp.riverside.ui.components.WithTopBar
import com.muijp.riverside.ui.navigation.FeedDetail
import com.muijp.riverside.ui.navigation.FeedSubscription
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(
    ExperimentalFoundationApi::class, FormatStringsInDatetimeFormats::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun StreamScreen(
    state: StreamUiState,
    onEvent: (StreamEvent) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current
    WithTopBar(
        title = state.title,
        navController = navController,
        actions = {
            EntriesFilter(
                selectedFilter = state.filter,
                onFilterSelected = { onEvent(StreamEvent.FilterSelected(it)) },
            )
        }
    ) {
        state.sections?.let { sections ->
            val pullToRefreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = state.isRefreshing,
                onRefresh = { onEvent(StreamEvent.PullToRefreshed) },
            ) {
                if (sections.isEmpty()) {
                    if (state.isNoFeedSubscribed) {
                        ContentUnavailableView(
                            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                            title = "No following feed",
                            subtitle = "Subscribe your favorite feeds to see them here",
                            action = ContentUnavailableAction(
                                title = "Subscribe feed",
                                action = { navController.navigate(FeedSubscription) },
                            ),
                        )
                    } else {
                        ContentUnavailableView(
                            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                            title = "You've read all entries",
                        )
                    }
                } else {
                    LazyColumn {
                        sections.forEach { section ->
                            stickyHeader {
                                Text(
                                    text = section.date.format(LocalDate.Format { byUnicodePattern("yyyy/MM/dd") }),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                )
                                HorizontalDivider(thickness = 0.5.dp)
                            }
                            itemsIndexed(
                                section.entries,
                                key = { _, entry -> entry.entry.url },
                            ) { index, entry ->
                                StreamItem(
                                    entry = entry,
                                    modifier = Modifier
                                        .animateItem(
                                            fadeInSpec = tween(500),
                                            placementSpec = tween(500),
                                            fadeOutSpec = tween(500)
                                        )
                                        .clickable {
                                            onEvent(StreamEvent.EntryClicked(context, entry))
                                        },
                                    onMarkAsRead = { onEvent(StreamEvent.EntryMarkedAsRead(it)) },
                                    onMarkAsUnread = { onEvent(StreamEvent.EntryMarkedAsUnread(it)) },
                                    onDelete = { onEvent(StreamEvent.EntryDeleted(it)) },
                                    onFeedTitleTap = { navController.navigate(FeedDetail(it)) },
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(bottom = if (index == section.entries.lastIndex) 16.dp else 0.dp),
                                    thickness = 0.5.dp,
                                )
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
                    onEvent(StreamEvent.Resumed)
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
fun StreamItem(
    entry: EntryWithFeedInfo,
    onMarkAsRead: (EntryWithFeedInfo) -> Unit,
    onMarkAsUnread: (EntryWithFeedInfo) -> Unit,
    onDelete: (EntryWithFeedInfo) -> Unit,
    onFeedTitleTap: (feedUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeListItem(
        startAction = if (BuildConfig.DEBUG) SwipeAction(
            icon = Icons.Default.Delete,
            background = MaterialTheme.colorScheme.error,
            action = { onDelete(entry) },
        ) else null,
        endAction = if (entry.entry.read) SwipeAction(
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
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val textColor by animateColorAsState(
                targetValue = MaterialTheme.colorScheme.onSurface.copy(alpha = if (entry.entry.read) 0.4f else 1.0f),
                animationSpec = tween(durationMillis = 500),
                label = "text color",
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                FeedImage(imageUrl = entry.feedImageUrl, size = 36.dp)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        entry.entry.title,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        entry.feedTitle,
                        modifier = Modifier.clickable {
                            onFeedTitleTap(entry.feedUrl)
                        },
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            entry.entry.content?.let {
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
