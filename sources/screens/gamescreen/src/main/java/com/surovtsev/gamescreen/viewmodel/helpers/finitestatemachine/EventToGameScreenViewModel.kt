package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.core.viewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.InitEvent

sealed class EventToGameScreenViewModel(
    override val setLoadingStateBeforeProcessing: Boolean = true,
): EventToViewModel.UserEvent() {

    object LoadGame: EventToGameScreenViewModel(), InitEvent
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


    object MandatoryEvents: EventToViewModel.MandatoryEvents(
        LoadGame,
    )
}
