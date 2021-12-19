package com.surovtsev.core.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.surovtsev.core.ui.theme.GrayBackground

@Composable
fun ErrorDialog(
    stateValue: ScreenStateValue<ScreenData>,
    screenCommandHandler: ScreenCommandHandler<CommandFromScreen>,
    closeErrorCommand: CommandFromScreen.CloseError,
) {
    val state by stateValue.observeAsState(ScreenState.Idle(
        ScreenData.NoData
    ))

    val errorMessage = (state as? ScreenState.Error<*>)?.message?: return

    val closeAction = { screenCommandHandler.handleCommand(closeErrorCommand) }

    Dialog(
        onDismissRequest = closeAction
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(GrayBackground)
                .padding(10.dp, 10.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Error",
                color = Color.Red
            )
            Text(
                text = errorMessage
            )
            Button(onClick = closeAction) {
                Text(text = "Ok")
            }
        }
    }
}