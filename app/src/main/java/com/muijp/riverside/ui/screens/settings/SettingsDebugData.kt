package com.muijp.riverside.ui.screens.settings

import com.muijp.riverside.data.models.Entry
import com.muijp.riverside.data.models.EntryWithFeedInfo
import kotlinx.datetime.Clock

class SettingsDebugData {
    companion object {
        fun entryWithFeedInfo(title: String, feedTitle: String): EntryWithFeedInfo =
            EntryWithFeedInfo(
                entry = Entry(
                    url = "https://example.com/blog/n",
                    title = title,
                    publishedAt = Clock.System.now(),
                    content = null,
                    feedUrl = "https://example.com",
                    read = false,
                ),
                feedUrl = "https://example.com/feed",
                feedTitle = feedTitle,
                feedImageUrl = null,
            )

        val entries: List<EntryWithFeedInfo> = listOf(
            entryWithFeedInfo(
                "他のモジュールの型を勝手に protocol に準拠させるのは避けたほうがよい",
                "maiyama4's blog"
            ),
            entryWithFeedInfo("Kotlin の型システムの基礎", "maiyama4's blog"),
            entryWithFeedInfo("Weekly Letter 34", "programming weekly"),
            entryWithFeedInfo("Weekly Letter 33", "programming weekly"),
            entryWithFeedInfo(
                "Xcode が Swift Package をビルドするとき #if DEBUG が適用されるかは Build Configuration の名前で決まる",
                "maiyama4's blog"
            ),
            entryWithFeedInfo("Weekly Letter 32", "programming weekly"),
            entryWithFeedInfo(
                "Unified Logging の出力をアプリから見られるようにする",
                "maiyama4's blog"
            ),
        )
    }
}
