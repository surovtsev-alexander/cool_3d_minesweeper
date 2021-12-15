package com.surovtsev.game.viewmodel

import com.surovtsev.core.viewmodel.CommandsFromScreen

sealed class CommandsFromGameScreen: CommandsFromScreen {
    object LoadGame: CommandsFromGameScreen()

    object NewGame: CommandsFromGameScreen()

    object CloseError: CommandsFromGameScreen()

    object Pause: CommandsFromGameScreen()

    object Resume: CommandsFromGameScreen()

    object OpenMenu: CommandsFromGameScreen()

    object CloseMenu: CommandsFromGameScreen()

    object GoToMainMenu: CommandsFromGameScreen()
}