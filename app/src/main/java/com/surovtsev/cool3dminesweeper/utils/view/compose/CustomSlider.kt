package com.surovtsev.cool3dminesweeper.utils.view.compose

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
import com.surovtsev.cool3dminesweeper.utils.math.SliderMath

typealias OnChangeAction = (newSliderPosition: Float) -> Unit

@Composable
fun CustomSliderWithCaption(
    name: String,
    borders: IntRange,
    value: Float,
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
                SliderMath.floatToInt(value).toString(),
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
            borders, value, onChangeAction
        )
    }
}

@Composable
fun CustomSlider(
    borders: IntRange,
    value: Float,
    onChangeAction: (Float) -> Unit,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

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
                    val newSliderPosition =
                        SliderMath.clipPosition(
                            value + diffSliderPosition,
                            borders
                        )
                    onChangeAction(
                        newSliderPosition
                    )
                    delta
                }
            )
    ) {
        SliderLine(
            value, borders
        )
    }
}

@Composable
fun SliderLine(
    value: Float,
    borders: IntRange
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(
                SliderMath.getRateByPosition(
                    value,
                    borders
                )
            )
            .background(PrimaryColor1)
    )
}
