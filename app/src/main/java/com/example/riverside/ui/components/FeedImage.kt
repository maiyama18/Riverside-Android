package com.example.riverside.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun FeedImage(
    imageUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(model = imageUrl)
    Box(
        modifier = modifier
            .size(size)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.1f))
    ) {
        Image(
            painter = painter,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )

        if (imageUrl == null || painter.state is AsyncImagePainter.State.Error) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
            ) {
                Image(
                    imageVector = Icons.Default.TextSnippet,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                )
            }
        }
    }
}
