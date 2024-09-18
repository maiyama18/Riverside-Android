package com.muijp.riverside.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.muijp.riverside.MainActivity
import com.muijp.riverside.R
import com.muijp.riverside.data.repositories.FeedRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

data class WidgetEntry(
    val url: String,
    val title: String,
    val feedTitle: String,
    val publishedAt: Instant,
)

class UnreadEntriesWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UnreadEntriesWidgetEntryPoint {
        fun feedRepository(): FeedRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val feedRepository = EntryPointAccessors.fromApplication(
            appContext,
            UnreadEntriesWidgetEntryPoint::class.java
        ).feedRepository()
        val unreadEntries = feedRepository.subscribedFeeds().firstOrNull()?.flatMap { feed ->
            feed.entries
                .filter { !it.read }
                .map { WidgetEntry(it.url, it.title, feed.title, it.publishedAt) }
        }?.sortedByDescending { it.publishedAt } ?: emptyList()

        val now = Clock.System.now()

        Log.d("Widget", "provideGlance ($id): unreadEntries=${unreadEntries.size}")

        provideContent {
            GlanceTheme {
                UnreadEntriesWidgetContent(context, unreadEntries, now)
            }
        }
    }
}

@Composable
fun UnreadEntriesWidgetContent(
    context: Context,
    unreadEntries: List<WidgetEntry>,
    now: Instant,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .clickable(
                onClick = actionStartActivity(Intent(context, MainActivity::class.java))
            ),
    ) {
        UnreadEntriesWidgetHeader(unreadEntries.size, now)
        UnreadEntriesWidgetEntryList(context, unreadEntries)
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun UnreadEntriesWidgetHeader(
    unreadEntryCount: Int,
    now: Instant,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surfaceVariant)
            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(R.drawable.widget_logo),
            contentDescription = null,
            modifier = GlanceModifier.size(20.dp).cornerRadius(4.dp),
        )

        Spacer(modifier = GlanceModifier.size(8.dp))

        if (unreadEntryCount > 0) {
            Row {
                Text(
                    unreadEntryCount.toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Spacer(modifier = GlanceModifier.size(4.dp))
                Text(
                    "unreads",
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
            }
        }

        Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            val updatedAtString = now.toLocalDateTime(TimeZone.currentSystemDefault())
                .format(LocalDateTime.Format { byUnicodePattern("HH:mm") })
            Text(
                "at $updatedAtString",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.secondary,
                ),
            )
        }
    }
}

@Composable
fun UnreadEntriesWidgetEntryList(context: Context, unreadEntries: List<WidgetEntry>) {
    if (unreadEntries.isEmpty()) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "No unread entries",
                style = TextStyle(
                    color = GlanceTheme.colors.secondary,
                    fontSize = 16.sp,
                ),
                modifier = GlanceModifier.padding(16.dp),
            )
        }
    } else {
        LazyColumn(modifier = GlanceModifier.fillMaxWidth()) {
            items(unreadEntries) { entry ->
                UnreadEntryItem(
                    entry,
                    modifier = GlanceModifier.fillMaxWidth().clickable(
                        onClick = actionStartActivity(
                            Intent(context, MainActivity::class.java)
                                .putExtra("entry_url_to_open", entry.url)
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun UnreadEntryItem(entry: WidgetEntry, modifier: GlanceModifier = GlanceModifier) {
    Column(modifier = modifier.padding(vertical = 4.dp, horizontal = 16.dp)) {
        Text(
            entry.feedTitle,
            style = TextStyle(
                color = GlanceTheme.colors.secondary,
                fontSize = 12.sp,
            ),
            maxLines = 1,
        )
        Text(
            entry.title,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 2,
        )
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 320)
@Composable
fun UnreadEntriesWidgetPreview() {
    GlanceTheme {
        UnreadEntriesWidgetContent(
            context = LocalContext.current,
            unreadEntries = listOf(
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Mastering Swift Concurrency: A Comprehensive Guide",
                    feedTitle = "maiyama4's blog",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "10 Python Tricks to Supercharge Your Code",
                    feedTitle = "Syntax Sugar",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Deep Dive into React Hooks: Beyond the Basics",
                    feedTitle = "Binary Brew",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "The Art of Clean Code: Best Practices for Maintainable Software",
                    feedTitle = "The Developer's Digest",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Building Scalable Microservices with Docker and Kubernetes",
                    feedTitle = "Future Stack: Tomorrow's Technology Today",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Optimizing Database Performance: Tips and Techniques",
                    feedTitle = "Data Dynamics: Navigating the Information Age",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Unified Logging の出力をアプリから見られるようにする",
                    feedTitle = "maiyama4's blog",
                    publishedAt = Instant.DISTANT_PAST,
                ),
            ),
            now = Instant.DISTANT_PAST,
        )
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 320)
@Composable
fun UnreadEntriesWidgetEmptyPreview() {
    GlanceTheme {
        UnreadEntriesWidgetContent(
            context = LocalContext.current,
            unreadEntries = emptyList(),
            now = Instant.DISTANT_PAST,
        )
    }
}
