package com.surovtsev.game.viewmodel

sealed class CommandsFromGameScreen {
    object LoadGame: CommandsFromGameScreen()

    object NewGame: CommandsFromGameScreen()

    object CloseError: CommandsFromGameScreen()

    object Pause: CommandsFromGameScreen()

    object Resume: CommandsFromGameScreen()

    object OpenMenu: CommandsFromGameScreen()

    object CloseMenu: CommandsFromGameScreen()
}