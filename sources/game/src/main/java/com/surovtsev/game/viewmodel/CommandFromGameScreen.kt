package com.surovtsev.game.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.viewmodel.CommandFromScreen

sealed interface CommandFromGameScreen: CommandFromScreen {
    class HandleScreenLeaving(owner: LifecycleOwner):
        CommandFromGameScreen,
        CommandFromScreen.HandleScreenLeaving(owner)

    object LoadGame: CommandFromGameScreen, CommandFromScreen.Init

    object NewGame: CommandFromGameScreen

    object CloseError: CommandFromGameScreen, CommandFromScreen.CloseError

    object CloseErrorAndFinish: CommandFromGameScreen, CommandFromScreen.CloseErrorAndFinish

    object OpenGameMenu: CommandFromGameScreen

    object CloseGameMenu: CommandFromGameScreen

    object GoToMainMenu: CommandFromGameScreen

    object CloseGame: CommandFromGameScreen

    object BaseCommands: CommandFromScreen.BaseCommands<CommandFromGameScreen>(
        LoadGame,
        CloseError,
        CloseErrorAndFinish,
        { HandleScreenLeaving(it) }
    )
}