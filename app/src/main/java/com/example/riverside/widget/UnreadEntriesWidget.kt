package com.example.riverside.widget

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
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
import com.example.riverside.R
import com.example.riverside.data.repositories.FeedRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Instant

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

        provideContent {
            GlanceTheme {
                UnreadEntriesWidgetContent(unreadEntries)
            }
        }
    }
}

@Composable
fun UnreadEntriesWidgetHeader(unreadEntryCount: Int) {
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
    }
}

@Composable
fun UnreadEntriesWidgetEntryList(unreadEntries: List<WidgetEntry>) {
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
                Column(modifier = GlanceModifier.padding(vertical = 4.dp, horizontal = 16.dp)) {
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
        }
    }
}

@Composable
fun UnreadEntriesWidgetContent(
    unreadEntries: List<WidgetEntry>,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground),
    ) {
        UnreadEntriesWidgetHeader(unreadEntries.size)
        UnreadEntriesWidgetEntryList(unreadEntries)
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 320)
@Composable
fun UnreadEntriesWidgetPreview() {
    GlanceTheme {
        UnreadEntriesWidgetContent(
            unreadEntries = listOf(
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "他のモジュールの型を勝手に protocol に準拠させるのは避けたほうがよい",
                    feedTitle = "maiyama4's blog",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Kotlin の型システムの基礎",
                    feedTitle = "maiyama4's blog",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Weekly Letter 34",
                    feedTitle = "programming weekly",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Weekly Letter 33",
                    feedTitle = "programming weekly",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Xcode が Swift Package をビルドするとき #if DEBUG が適用されるかは Build Configuration の名前で決まる",
                    feedTitle = "maiyama4's blog",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Weekly Letter 32",
                    feedTitle = "programming weekly",
                    publishedAt = Instant.DISTANT_PAST,
                ),
                WidgetEntry(
                    url = "https://example.com/blog/n",
                    title = "Unified Logging の出力をアプリから見られるようにする",
                    feedTitle = "maiyama4's blog",
                    publishedAt = Instant.DISTANT_PAST,
                ),
            )
        )
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 320)
@Composable
fun UnreadEntriesWidgetEmptyPreview() {
    GlanceTheme {
        UnreadEntriesWidgetContent(unreadEntries = emptyList())
    }
}
