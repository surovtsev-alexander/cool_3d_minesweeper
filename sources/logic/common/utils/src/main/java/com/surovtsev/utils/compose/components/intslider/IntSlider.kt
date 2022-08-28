/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


fun IntRange.toMathString(): String =
    "[${this.first}; ${this.last}]"

object IntSliderConstants {
    val lineColor = Color.White
    val backgroundColor = Color.Black
}

@Composable
fun IntSlider(
    position: Int,
    onChange: IntSliderOnChange,
    borders: IntRange,
    lineColor: Color = IntSliderConstants.lineColor,
    backgroundColor: Color = IntSliderConstants.backgroundColor,
    name: String? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val sliderContext: SliderContext by remember{ mutableStateOf(SliderContext(coroutineScope).apply {
        this.borders.value = borders
        this.onChange.value = onChange
        setPosition(position)
    }) }
    DisposableEffect(sliderContext) {
        onDispose {
            sliderContext.stopJob()
        }
    }


    Column {
        if (name != null) {
            SliderCaption(
                name = name,
                sliderContext = sliderContext,
            )
        }

        LaunchedEffect(key1 = borders, key2 = position, key3 = onChange) {
            sliderContext.borders.value = borders
            sliderContext.setPosition(position)
            sliderContext.onChange.value = onChange
        }


        val scrollableState = rememberScrollableState { delta ->
            coroutineScope.launch(Dispatchers.IO) {
                sliderContext.slideAlt(delta)
            }
            delta
        }

        val isScrollInProgress = scrollableState.isScrollInProgress
        val screenOffset by sliderContext.lineWidthRate.collectAsState()
        LaunchedEffect(key1 = isScrollInProgress, key2 = screenOffset) {
            sliderContext.isScrollInProgress.value = isScrollInProgress

            if (isScrollInProgress) {
                return@LaunchedEffect
            }

            coroutineScope.launch {
                scrollableState.scrollBy(screenOffset)
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .border(1.dp, Color.Black)
                .background(backgroundColor)
                .onGloballyPositioned { coordinates ->
                    sliderContext.layoutWidth.value = coordinates.size.width
                }
                .scrollable(
                    orientation = Orientation.Horizontal,
                    state = scrollableState
                )
        ) {
            SliderLine(
                sliderContext.progress,
                lineColor
            )
        }
    }
}

@Composable
fun SliderCaption(
    name: String,
    sliderContext: SliderContext,
) {
    val position by sliderContext.sliderPosition.collectAsState()
    val borders by sliderContext.borders.collectAsState()
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
}

@Composable
fun SliderLine(
    lineWidthRate: Flow<Float>,
    lineColor: Color,
) {
    val widthRate by lineWidthRate.collectAsState(initial = 0f)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(
                widthRate
            )
            .background(lineColor)
    )
}
