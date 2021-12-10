package com.surovtsev.cool3dminesweeper.utils.view.compose.components

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
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.LightBlue
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.PrimaryColor1
import com.surovtsev.utils.math.SliderMath

typealias OnChangeAction = (newSliderPosition: Int) -> Unit

@Composable
fun CustomSliderWithCaption(
    name: String,
    borders: IntRange,
    sliderPosition: Int,
    onChangeAction: OnChangeAction,
) {
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
            borders, sliderPosition, onChangeAction
        )
    }
}

@Composable
fun CustomSlider(
    borders: IntRange,
    sliderPosition: Int,
    onChangeAction: OnChangeAction,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var prevPosition by remember { mutableStateOf(sliderPosition) }
    var actualFloatPosition by remember { mutableStateOf(SliderMath.intToFloat(sliderPosition)) }

    if (prevPosition != sliderPosition) {
        prevPosition = sliderPosition
        actualFloatPosition = sliderPosition.toFloat()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .border(1.dp, Color.Black)
            .background(LightBlue)
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
                        onChangeAction(
                            newSliderPosition
                        )
                    }
                    delta
                }
            )
    ) {
        SliderLine(
            actualFloatPosition, borders
        )
    }
}

@Composable
fun SliderLine(
    sliderPosition: Float,
    borders: IntRange
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
            .background(PrimaryColor1)
    )
}
