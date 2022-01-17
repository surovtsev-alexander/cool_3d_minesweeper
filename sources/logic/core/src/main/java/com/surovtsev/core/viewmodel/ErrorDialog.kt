package com.surovtsev.core.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.surovtsev.core.ui.theme.GrayBackground
import com.surovtsev.finitestatemachine.state.StateDescription

@Composable
fun ErrorDialog(
    screenStateFlow: ScreenStateFlow<ScreenData>,
    eventHandler: EventHandler<EventToViewModel>,
    closeErrorEvent: EventToViewModel,
    closeErrorAndFinishEvent: EventToViewModel
) {
    val state by screenStateFlow.collectAsState()

    val errorMessage = (state.description as? StateDescription.Error)?.message?: return

    val closeAction = { eventHandler.handleEvent(closeErrorEvent) }

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
                        eventHandler.handleEvent(
                            closeErrorAndFinishEvent
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
fun <C: EventToViewModel, D: ScreenData> ErrorDialogPlacer<C, D>.PlaceErrorDialog() {
    @Suppress("UNCHECKED_CAST")
    ErrorDialog(
        screenStateFlow = screenStateFlow,
        eventHandler = this as EventHandler<EventToViewModel>,
        closeErrorEvent = mandatoryEvents.closeError,
        closeErrorAndFinishEvent = mandatoryEvents.closeErrorAndFinish,
    )
}