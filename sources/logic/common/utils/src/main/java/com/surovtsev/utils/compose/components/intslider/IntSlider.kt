package com.surovtsev.utils.compose.components.intslider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs


typealias IntSliderOnChange = (newValue: Int) -> Unit

fun IntRange.toMathString(): String =
    "[${this.first}; ${this.last}]"

object IntSliderConstants {
    val lineColor = Color.White
    val backgroundColor = Color.Black
}

@Composable
fun IntSliderWithCaption(
    position: Int,
    onChange: IntSliderOnChange,
    borders: IntRange,
    lineColor: Color = IntSliderConstants.lineColor,
    backgroundColor: Color = IntSliderConstants.backgroundColor,
    name: String = "",
) {
    Column {
        Row {
            Text(
                name,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.33f)
            )
            Text(
                position.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            Text(
                borders.toMathString(),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
        IntSlider(
            position,
            onChange,
            borders,
            lineColor,
            backgroundColor,
        )
    }
}

@Composable
fun IntSlider(
    position: Int,
    onChange: IntSliderOnChange,
    borders: IntRange,
    lineColor: Color = IntSliderConstants.lineColor,
    backgroundColor: Color = IntSliderConstants.backgroundColor,
) {
    val coroutineScope = rememberCoroutineScope()

    var width by remember { mutableStateOf(0) }
    var sliderContextVar: SliderContext by remember{ mutableStateOf(SliderContext(borders, width, position)) }

    LaunchedEffect(key1 = borders, key2 = width) {
        sliderContextVar = SliderContext(borders, width, position)
    }

    val sliderContext = sliderContextVar

    LaunchedEffect(key1 = position) {
        sliderContext.setPosition(position)
    }

    val scrollableState = rememberScrollableState { delta ->
        sliderContext.slide(
            delta,
            onChange
        )
        delta
    }

    sliderContext.isScrollInProgress = scrollableState.isScrollInProgress

    if (!scrollableState.isScrollInProgress) {
        coroutineScope.launch {
            val prevDelta = sliderContext.prevDelta
            if (abs(prevDelta) > 1f) {
                scrollableState.scrollBy(
                    -1 * prevDelta
                )
            } else {
                sliderContext.erasePrevDelta()
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .border(1.dp, Color.Black)
            .background(backgroundColor)
            .onGloballyPositioned { coordinates ->
                width = coordinates.size.width
            }
            .scrollable(
                orientation = Orientation.Horizontal,
                state = scrollableState
            )
    ) {
        SliderLine(
            sliderContext.lineWidthRate,
            lineColor
        )
    }
}

@Composable
fun SliderLine(
    lineWidthRate: StateFlow<Float>,
    lineColor: Color,
) {
    val widthRate by lineWidthRate.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(
                widthRate
            )
            .background(lineColor)
    )
}
