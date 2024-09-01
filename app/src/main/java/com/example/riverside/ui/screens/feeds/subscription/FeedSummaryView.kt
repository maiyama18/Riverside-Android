package com.example.riverside.ui.screens.feeds.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.riverside.data.models.Entry
import com.example.riverside.data.models.Feed
import com.example.riverside.ui.components.FeedImage
import kotlinx.datetime.Instant

@Composable
fun FeedSummaryView(
    feed: Feed,
    feedAlreadySubscribed: Boolean,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.scrim.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        FeedImage(imageUrl = feed.imageUrl, size = 48.dp)

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                feed.host?.let { host ->
                    Text(
                        host,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
                Text(feed.title, fontWeight = FontWeight.Bold)
            }
            if (feed.overview != null) {
                Text(
                    feed.overview,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                "${feed.entries.size} entries",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Button(
                onClick = onSubscribeClick,
                modifier = Modifier.align(Alignment.End),
                enabled = !feedAlreadySubscribed,
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    if (feedAlreadySubscribed) "Already subscribed" else "Subscribe",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun FeedSummaryViewPreview() {
    Box(modifier = Modifier.padding(8.dp)) {
        FeedSummaryView(
            feed = Feed(
                url = "https://maiyama4.hatenablog.com/feed",
                title = "maiyama4's blog",
                pageUrl = "https://maiyama4.hatenablog.com",
                imageUrl = "https://maiyama4.hatenablog.com/favicon",
                overview = "This is a blog primarily focused on iOS development. It is updated approximately once a week.",
                entries = listOf(
                    Entry(
                        url = "https://maiyama4.hatenablog.com/entry/2021/09/01/000000",
                        title = "How to use SwiftUI's @StateObject",
                        publishedAt = Instant.parse("2021-09-01T00:00:00Z"),
                        content = "This article explains how to use SwiftUI's @StateObject property wrapper.",
                        read = false,
                    ),
                    Entry(
                        url = "https://maiyama4.hatenablog.com/entry/2021/08/25/000000",
                        title = "Introduction to Swift Concurrency",
                        publishedAt = Instant.parse("2021-08-25T00:00:00Z"),
                        content = "This article introduces Swift Concurrency, a new feature in Swift 5.5.",
                        read = false,
                    ),
                ),
            ),
            feedAlreadySubscribed = false,
            onSubscribeClick = {},
        )
    }
}