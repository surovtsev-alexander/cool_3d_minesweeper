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
import com.surovtsev.finitestatemachine.interfaces.EventReceiver
import com.surovtsev.finitestatemachine.state.description.Description

@Composable
fun ErrorDialog(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
) {
    val state by screenStateFlow.collectAsState()

    val errorMessage = (state.description as? Description.Error)?.message?: return

    val closeAction: () -> Unit = {
        eventReceiver.receiveEvent(
            EventToViewModel.CloseError
        )
    }

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
                        eventReceiver.receiveEvent(
                            EventToViewModel.CloseErrorAndFinish
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
fun ErrorDialogPlacer.PlaceErrorDialog(
) {
    @Suppress("UNCHECKED_CAST")
    ErrorDialog(
        screenStateFlow = screenStateFlow,
        eventReceiver = finiteStateMachine as EventReceiver,
    )
}
