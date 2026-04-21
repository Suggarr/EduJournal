package com.edujournal.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ScrollAwareAddFab(
    listState: LazyListState,
    onClick: () -> Unit,
    contentDescription: String,
    enabled: Boolean = true
) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        var previousIndex = 0
        var previousOffset = 0

        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collectLatest { (index, offset) ->
                val scrollingDown =
                    index > previousIndex || (index == previousIndex && offset > previousOffset)
                val scrollingUp =
                    index < previousIndex || (index == previousIndex && offset < previousOffset)

                if (scrollingDown) isVisible = false
                if (scrollingUp) isVisible = true

                previousIndex = index
                previousOffset = offset
            }
    }

    AnimatedVisibility(
        visible = enabled && isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 180)) +
            scaleIn(initialScale = 0.9f, animationSpec = tween(durationMillis = 180)),
        exit = fadeOut(animationSpec = tween(durationMillis = 120)) +
            scaleOut(targetScale = 0.9f, animationSpec = tween(durationMillis = 120))
    ) {
        FloatingActionButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = contentDescription
            )
        }
    }
}
