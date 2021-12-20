package com.surovtsev.game.viewmodel

import com.surovtsev.core.viewmodel.CommandFromScreen

sealed class CommandFromGameScreen: CommandFromScreen {
    object LoadGame: CommandFromGameScreen()

    object NewGame: CommandFromGameScreen()

    object CloseError: CommandFromGameScreen(), CommandFromScreen.CloseError

    object CloseErrorAndFinish: CommandFromGameScreen(), CommandFromScreen.CloseErrorAndFinish

    object Pause: CommandFromGameScreen()

    object Resume: CommandFromGameScreen()

    object OpenMenu: CommandFromGameScreen()

    object CloseMenu: CommandFromGameScreen()

    object GoToMainMenu: CommandFromGameScreen()

    object CloseGame: CommandFromGameScreen()
}