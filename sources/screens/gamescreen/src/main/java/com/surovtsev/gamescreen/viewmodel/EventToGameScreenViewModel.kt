package com.surovtsev.gamescreen.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.viewmodel.EventToViewModel

sealed class EventToGameScreenViewModel(
    override val setLoadingStateBeforeProcessing: Boolean = true
): EventToViewModel {
    class HandleScreenLeaving(
        override val owner: LifecycleOwner
    ):
        EventToGameScreenViewModel(),
        EventToViewModel.HandleScreenLeaving

    object LoadGame: EventToGameScreenViewModel(), EventToViewModel.Init
    object NewGame: EventToGameScreenViewModel()

    object CloseError: EventToGameScreenViewModel(), EventToViewModel.CloseError
    object CloseErrorAndFinish: EventToGameScreenViewModel(), EventToViewModel.CloseErrorAndFinish

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


    object MandatoryEvents: EventToViewModel.MandatoryEvents<EventToGameScreenViewModel>(
        LoadGame,
        CloseError,
        CloseErrorAndFinish,
        { HandleScreenLeaving(it) }
    )
}