package com.example.riverside.ui.screens.feeds.detail

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.riverside.data.models.Entry
import com.example.riverside.ui.components.WithTopBar

fun launchCustomTabs(context: Context, url: String) {
    CustomTabsIntent.Builder()
        .setUrlBarHidingEnabled(false)
        .build()
        .launchUrl(context, Uri.parse(url))
}

@Composable
fun FeedDetailScreen(
    state: FeedDetailUiState,
    onEvent: (FeedDetailEvent) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current
    WithTopBar(title = state.feed?.title ?: "", navController = navController) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(
                state.entries.filter { !it.read },
                key = { _, entry -> entry.url }) { index, entry ->
                EntryListItem(
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
                    entry = entry,
                )
                if (index < state.entries.lastIndex) {
                    Divider()
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
fun EntryListItem(entry: Entry, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
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
