package com.surovtsev.gamelogic.minesweeper.interaction.ui

import kotlinx.coroutines.flow.MutableStateFlow

class UIGameControlsMutableFlows(
    val flagging: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val uiGameStatus: MutableStateFlow<UIGameStatus> = MutableStateFlow(UIGameStatus.Unimportant),
)
