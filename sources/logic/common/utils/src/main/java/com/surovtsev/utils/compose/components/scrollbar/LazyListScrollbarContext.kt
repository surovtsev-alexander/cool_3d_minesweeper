package com.surovtsev.utils.compose.components.scrollbar

import android.os.SystemClock
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.abs
import kotlin.math.roundToInt

class LazyListScrollbarContext(
    val lazyListState: LazyListState,
    val dipCoefficient: Float,
    private val minimalHeight: Float = 50.dp.value,
    val widthDp: Dp = 20.dp
) {
    private val _triggerUpdateFlow = MutableSharedFlow<Long>()
    val triggerUpdateFlow = _triggerUpdateFlow.asSharedFlow()

    suspend fun triggerUpdate() {
        _triggerUpdateFlow.emit(SystemClock.elapsedRealtime())
    }

    var viewHeight = 0f
        private set

    var startPos = 0f
        private set

    var scrollBarHeight = 0f
        private set

    var sideHeight = 0f
        private set

    var elementsCount = 0
        private set

    var firstVisibleItemIndex = 0
        private set
    var lastVisibleItemIndex = 0
        private set


    var needToRecalculateForward = true
        private set


    var scrollBarIsFull = true
        private set

    var isClosedToTop = true
        private set

    var isClosedToBottom = true
        private set

    var progress = 0f
        private set

    var approximateOneItemOffset = 0f
        private set

    var offsetRemains = 0f
        private set

    fun updateElementsCount(newElementsCount: Int) {
        if (newElementsCount != elementsCount) {
            elementsCount = newElementsCount

            needToRecalculateForward = true
        }
    }

    fun updateViewHeight(newValue: Float) {
        if (newValue != viewHeight) {
            viewHeight = newValue

            needToRecalculateForward = true
        }
    }

    fun updateVisibleItems(
        newFirstVisibleItem: Int,
        newLastVisibleItem: Int,
    ) {
        if (
            newFirstVisibleItem != firstVisibleItemIndex ||
            newLastVisibleItem != lastVisibleItemIndex
        ) {
            firstVisibleItemIndex = newFirstVisibleItem
            lastVisibleItemIndex = newLastVisibleItem

            needToRecalculateForward = true
        }
    }


    fun recalculateForwardIfNeeded() {
        if (!needToRecalculateForward) {
            return
        }

        scrollBarIsFull = elementsCount == 0 || lastVisibleItemIndex == 0 || abs(0f - viewHeight) < 1f
        isClosedToTop = firstVisibleItemIndex == 0
        isClosedToBottom = lastVisibleItemIndex == elementsCount - 1

        val rawHeight = if (scrollBarIsFull) {
            viewHeight
        } else {
            (lastVisibleItemIndex - firstVisibleItemIndex + 1) * viewHeight / elementsCount
        }

        val heightIsInsufficient = rawHeight >= minimalHeight

        scrollBarHeight = if (scrollBarIsFull || heightIsInsufficient) {
            rawHeight
        } else {
            minimalHeight
        }

        val scrollBarDiffHeightToFitMinimalHeight = scrollBarHeight - rawHeight

        approximateOneItemOffset = if(elementsCount == 0) {
            0f
        } else {
            (viewHeight - scrollBarDiffHeightToFitMinimalHeight) / elementsCount
        }

        startPos = when {
            scrollBarIsFull -> {
                0f
            }
            isClosedToTop -> {
                0f
            }
            isClosedToBottom -> {
                viewHeight - scrollBarHeight
            }
            else -> {

                approximateOneItemOffset * firstVisibleItemIndex
            }
        }

        sideHeight = viewHeight - scrollBarHeight

        needToRecalculateForward = false
    }

    fun calculateIndexDiff(
        offset: Float
    ): Int {
        if (abs(approximateOneItemOffset) < 1e-8f) {
            return 0
        }

        offsetRemains += offset
        val res = (offsetRemains / dipCoefficient / approximateOneItemOffset).roundToInt()

        offsetRemains -= res * dipCoefficient * approximateOneItemOffset

        return res
    }

    suspend fun moveIndex(
        diff: Int
    ) {

        if (diff == 0) {
            return
        }

        val newFirstVisibleItemIndex = (firstVisibleItemIndex + diff).run {
            when {
                this <= 0 -> {
                    offsetRemains = 0f
                    0
                }
                this >= elementsCount -> {
                    offsetRemains = 0f
                    elementsCount - 1
                }
                else -> {
                    this
                }
            }
        }

        if (newFirstVisibleItemIndex != firstVisibleItemIndex) {
            lazyListState.scrollToItem(newFirstVisibleItemIndex, 0)
        }
    }

    fun recalculateProgress() {
        progress = when {
            scrollBarIsFull -> {
                1f
            }
            isClosedToTop -> {
                0f
            }
            isClosedToBottom -> {
                1f
            }
            else -> {
                (firstVisibleItemIndex + lastVisibleItemIndex) * 0.5f / elementsCount
            }
        }
    }
}
