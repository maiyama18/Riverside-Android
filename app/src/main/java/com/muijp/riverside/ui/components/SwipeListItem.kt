package com.muijp.riverside.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class SwipeAction(
    val icon: ImageVector,
    val background: Color,
    val action: () -> Unit,
)

@Composable
fun SwipeListItem(
    modifier: Modifier = Modifier,
    startAction: SwipeAction? = null,
    endAction: SwipeAction? = null,
    content: @Composable () -> Unit,
) {
    val updatedStartAction = rememberUpdatedState(startAction)
    val updatedEndAction = rememberUpdatedState(endAction)
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> updatedStartAction.value?.action?.invoke()
                SwipeToDismissBoxValue.EndToStart -> updatedEndAction.value?.action?.invoke()
                SwipeToDismissBoxValue.Settled -> {}
            }
            return@rememberSwipeToDismissBoxState it == SwipeToDismissBoxValue.StartToEnd
        }
    )
    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier,
        backgroundContent = {
            val color = when (swipeToDismissBoxState.targetValue) {
                SwipeToDismissBoxValue.StartToEnd -> startAction?.background ?: Color.Transparent
                SwipeToDismissBoxValue.EndToStart -> endAction?.background ?: Color.Transparent
                SwipeToDismissBoxValue.Settled -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = when (swipeToDismissBoxState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                    SwipeToDismissBoxValue.Settled -> Alignment.Center
                }
            ) {
                val icon = when (swipeToDismissBoxState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> startAction?.icon
                    SwipeToDismissBoxValue.EndToStart -> endAction?.icon
                    SwipeToDismissBoxValue.Settled -> null
                }
                icon?.let {
                    Icon(imageVector = it, contentDescription = null, tint = Color.White)
                }
            }
        },
        enableDismissFromStartToEnd = startAction != null,
        enableDismissFromEndToStart = endAction != null,
    ) {
        content()
    }
}