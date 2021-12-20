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

    object Pause: CommandFromGameScreen

    object Resume: CommandFromGameScreen

    object OpenMenu: CommandFromGameScreen

    object CloseMenu: CommandFromGameScreen

    object GoToMainMenu: CommandFromGameScreen

    object CloseGame: CommandFromGameScreen
}