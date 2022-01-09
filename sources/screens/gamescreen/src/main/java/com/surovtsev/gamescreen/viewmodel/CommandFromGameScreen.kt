package com.surovtsev.gamescreen.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.viewmodel.CommandFromScreen

sealed class CommandFromGameScreen(
    override val setLoadingStateWhileProcessing: Boolean = true
): CommandFromScreen {
    class HandleScreenLeaving(
        override val owner: LifecycleOwner
    ):
        CommandFromGameScreen(),
        CommandFromScreen.HandleScreenLeaving

    object LoadGame: CommandFromGameScreen(), CommandFromScreen.Init
    object NewGame: CommandFromGameScreen()

    object CloseError: CommandFromGameScreen(), CommandFromScreen.CloseError
    object CloseErrorAndFinish: CommandFromGameScreen(), CommandFromScreen.CloseErrorAndFinish

    object GoToMainMenu: CommandFromGameScreen()

    object OpenGameMenuAndSetLoadingState: CommandFromGameScreen()
    object OpenGameMenuAndSetIdleState: CommandFromGameScreen()
    object SetIdleState: CommandFromGameScreen()
    object CloseGameMenu: CommandFromGameScreen()


    object RemoveFlaggedBombs: CommandFromGameScreen(
        setLoadingStateWhileProcessing = false
    )
    object RemoveOpenedSlices: CommandFromGameScreen(
        setLoadingStateWhileProcessing = false
    )
    object ToggleFlagging: CommandFromGameScreen(
        setLoadingStateWhileProcessing = false
    )

    object CloseGameStatusDialog: CommandFromGameScreen()


    object BaseCommands: CommandFromScreen.BaseCommands<CommandFromGameScreen>(
        LoadGame,
        CloseError,
        CloseErrorAndFinish,
        { HandleScreenLeaving(it) }
    )
}