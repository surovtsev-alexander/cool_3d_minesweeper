package com.surovtsev.gamescreen.minesweeper.interaction.commandhandler

sealed interface CommandToMinesweeper {
    interface CanBeSkipped

    object NewGame: CommandToMinesweeper

    object LoadGame: CommandToMinesweeper

    object SaveGame: CommandToMinesweeper

    object Tick: CommandToMinesweeper, CanBeSkipped
}