package com.surovtsev.core.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    closeErrorAndFinishCommand: CommandFromScreen.CloseErrorAndFinish,
    noData: ScreenData.NoData
) {
    val state by stateValue.observeAsState(ScreenState.Idle(
        noData
    ))

    val errorMessage = (state as? ScreenState.Error<*>)?.message?: return

    val closeAction = { screenCommandHandler.handleCommand(closeErrorCommand) }

    Dialog(
        onDismissRequest = closeAction
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = closeAction) {
                    Text(text = "ok")
                }
                Button(
                    onClick = {
                        screenCommandHandler.handleCommand(
                            closeErrorAndFinishCommand
                        )
                    }
                ) {
                    Text(text = "to main menu")
                }
            }
        }
    }
}

@Composable
fun <C: CommandFromScreen, D: ScreenData> ErrorDialogPlacer<C, D>.PlaceErrorDialog() {
    @Suppress("UNCHECKED_CAST")
    ErrorDialog(
        stateValue = state  as ScreenStateValue<ScreenData>,
        screenCommandHandler = this as ScreenCommandHandler<CommandFromScreen>,
        closeErrorCommand = baseCommands.closeError,
        closeErrorAndFinishCommand = baseCommands.closeErrorAndFinish,
        noData = noScreenData as ScreenData.NoData,
    )
}