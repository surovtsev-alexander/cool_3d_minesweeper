package com.surovtsev.gamescreen.minesweeper.commandhandler

sealed interface CommandToMinesweeper {
    object NewGame: CommandToMinesweeper

    object LoadGame: CommandToMinesweeper

    object SaveGame: CommandToMinesweeper

    object Pause: CommandToMinesweeper

    object Resume: CommandToMinesweeper

    object Tick: CommandToMinesweeper
}