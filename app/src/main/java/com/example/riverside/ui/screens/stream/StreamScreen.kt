package com.example.riverside.ui.screens.stream

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.riverside.BuildConfig
import com.example.riverside.ui.components.FeedImage
import com.example.riverside.ui.components.WithTopBar
import com.example.riverside.ui.navigation.FeedDetail
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(ExperimentalFoundationApi::class, FormatStringsInDatetimeFormats::class)
@Composable
fun StreamScreen(
    state: StreamUiState,
    onEvent: (StreamEvent) -> Unit,
    navController: NavHostController,
) {
    WithTopBar(title = "Stream", navController = navController) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            state.sections?.let { sections ->
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                        items(section.entries) { entry ->
                            StreamItem(
                                entry = entry,
                                onMarkAsRead = { onEvent(StreamEvent.EntryMarkedAsRead(it)) },
                                onDelete = { onEvent(StreamEvent.EntryDeleted(it)) },
                                onFeedTitleTap = { navController.navigate(FeedDetail(it)) },
                            )
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreamItem(
    entry: StreamEntry,
    onMarkAsRead: (StreamEntry) -> Unit,
    onDelete: (StreamEntry) -> Unit,
    onFeedTitleTap: (feedUrl: String) -> Unit,
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
        enableDismissFromEndToStart = !entry.entry.read,
        enableDismissFromStartToEnd = BuildConfig.DEBUG,
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
                        color = MaterialTheme.colorScheme.onSurface,
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
