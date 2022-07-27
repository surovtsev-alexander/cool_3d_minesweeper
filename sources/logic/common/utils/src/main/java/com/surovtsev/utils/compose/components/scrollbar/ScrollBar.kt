package com.surovtsev.utils.compose.components.scrollbar

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun ScrollBar(
    modifier: Modifier,
    lazyListScrollableContext: LazyListScrollbarContext,
) {
//    Log.d("TEST", "ScrollBar recomposition")

    val triggerUpdate by lazyListScrollableContext.triggerUpdateFlow.collectAsState(initial = false)

//    Log.d("TEST", "triggerUpdate: $triggerUpdate")

    val coroutineScope = rememberCoroutineScope()

    val scrollableState = rememberScrollableState { delta ->
        val indexDiff = lazyListScrollableContext.calculateIndexDiff(delta)

        coroutineScope.launch {
            lazyListScrollableContext.moveIndex(
                indexDiff
            )
        }

        delta
    }

    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                orientation = Orientation.Vertical,
                state = scrollableState,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(lazyListScrollableContext.widthDp),
            horizontalAlignment = Alignment.End
        ) {
            with (lazyListScrollableContext) {
                updateViewHeight(this@BoxWithConstraints.maxHeight.value)
                updateVisibleItems(
                    lazyListState.firstVisibleItemIndex,
                    lazyListState.lastVisibleItemIndex,
                )

                recalculateForwardIfNeeded()
            }

            Spacer(
                modifier = Modifier
                    .height(lazyListScrollableContext.startPos.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(lazyListScrollableContext.scrollBarHeight.dp)
                    .background(Color.Gray)
            )
        }
    }
}

private val LazyListState.lastVisibleItemIndex: Int
    @Composable get() = remember {
        derivedStateOf { layoutInfo.visibleItemsInfo.lastOrNull()?.index }
    }.value ?: 0