package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel

sealed class EventToGameScreenViewModel(
    override val setLoadingStateBeforeProcessing: Boolean = true,
): EventToViewModel.UserEvent() {

    companion object {
        @Suppress("FunctionName")
        fun LoadGame() = EventToViewModel.Init
    }

    object NewGame: EventToGameScreenViewModel()

    object GoToMainMenu: EventToGameScreenViewModel()

    object OpenGameMenuAndSetLoadingState: EventToGameScreenViewModel()
    object OpenGameMenuAndSetIdleState: EventToGameScreenViewModel()
    object SetIdleState: EventToGameScreenViewModel()
    object CloseGameMenu: EventToGameScreenViewModel()

    object RemoveFlaggedBombs: EventToGameScreenViewModel(
        setLoadingStateBeforeProcessing = false
    )
    object RemoveOpenedSlices: EventToGameScreenViewModel(
        setLoadingStateBeforeProcessing = false
    )
    object ToggleFlagging: EventToGameScreenViewModel(
        setLoadingStateBeforeProcessing = false
    )

    object CloseGameStatusDialog: EventToGameScreenViewModel()
}
