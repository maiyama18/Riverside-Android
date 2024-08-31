package com.example.riverside.ui.screens.feeds.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.riverside.data.network.FeedResponse
import com.example.riverside.ui.components.FeedImage
import io.ktor.http.Url

@Composable
fun FeedSummaryView(
    feed: FeedResponse,
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
                val host = Url(feed.url).host
                if (host.isNotEmpty()) {
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

            Button(
                onClick = onSubscribeClick,
                modifier = Modifier.align(Alignment.End),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text("Subscribe", style = MaterialTheme.typography.labelMedium)
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun FeedSummaryViewPreview() {
    Box(modifier = Modifier.padding(8.dp)) {
        FeedSummaryView(
            feed = FeedResponse(
                url = "https://maiyama4.hatenablog.com/feed",
                title = "maiyama4's blog",
                pageUrl = "https://maiyama4.hatenablog.com",
                imageUrl = "https://maiyama4.hatenablog.com/favicon",
                overview = "This is a blog primarily focused on iOS development. It is updated approximately once a week.",
            ),
            feedAlreadySubscribed = false,
            onSubscribeClick = {},
        )
    }
}