package com.surovtsev.utils.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.surovtsev.utils.math.SliderMath
import kotlinx.coroutines.flow.MutableStateFlow

typealias SliderPositionMutableStateFlow = MutableStateFlow<Int>

@Composable
fun CustomSliderWithCaption(
    name: String,
    borders: IntRange,
    sliderPositionMutableStateFlow: SliderPositionMutableStateFlow,
    backgroundColor: Color,
    lineColor: Color,
) {
    val sliderPosition: Int by sliderPositionMutableStateFlow.collectAsState()
    Column {
        Row {
            Text(
                name,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.33f)
            )
            Text(
                sliderPosition.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            Text(
                "(${borders})",
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
        CustomSlider(
            borders, sliderPositionMutableStateFlow, backgroundColor, lineColor
        )
    }
}

@Composable
fun CustomSlider(
    borders: IntRange,
    sliderPositionMutableStateFlow: SliderPositionMutableStateFlow,
    backgroundColor: Color,
    lineColor: Color
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val sliderPosition: Int by sliderPositionMutableStateFlow.collectAsState()
    var prevPosition by remember { mutableStateOf(sliderPosition) }
    var actualFloatPosition by remember { mutableStateOf(SliderMath.intToFloat(sliderPosition)) }

    val samePosition = prevPosition == sliderPosition

    if (!samePosition) {
        prevPosition = sliderPosition
        actualFloatPosition = sliderPosition.toFloat()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .border(1.dp, Color.Black)
            .background(backgroundColor)
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            }
            .scrollable(
                orientation = Orientation.Horizontal,
                state = rememberScrollableState { delta ->
                    val normalizedDelta = delta / size.width
                    val diffSliderPosition = SliderMath.getDiffByRate(
                        normalizedDelta, borders
                    )
                    actualFloatPosition =
                        SliderMath.clipPosition(
                            actualFloatPosition + diffSliderPosition,
                            borders
                        )

                    val newSliderPosition = SliderMath.floatToInt(actualFloatPosition)

                    if (newSliderPosition != prevPosition) {
                        prevPosition = newSliderPosition

                        sliderPositionMutableStateFlow.value = newSliderPosition
                    }
                    delta
                }
            )
    ) {
        SliderLine(
            actualFloatPosition, borders, lineColor
        )
    }
}

@Composable
fun SliderLine(
    sliderPosition: Float,
    borders: IntRange,
    lineColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(
                SliderMath.getRateByPosition(
                    sliderPosition,
                    borders
                )
            )
            .background(lineColor)
    )
}
