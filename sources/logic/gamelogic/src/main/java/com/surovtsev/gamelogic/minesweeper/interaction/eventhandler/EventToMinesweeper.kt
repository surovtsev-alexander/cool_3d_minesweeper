package com.surovtsev.gamelogic.minesweeper.interaction.eventhandler

sealed interface EventToMinesweeper {
    interface CanBeSkipped

    object NewGame: EventToMinesweeper

    object LoadGame: EventToMinesweeper

    object SaveGame: EventToMinesweeper

    object SetGameStateToNull: EventToMinesweeper

    object Tick: EventToMinesweeper, CanBeSkipped
}