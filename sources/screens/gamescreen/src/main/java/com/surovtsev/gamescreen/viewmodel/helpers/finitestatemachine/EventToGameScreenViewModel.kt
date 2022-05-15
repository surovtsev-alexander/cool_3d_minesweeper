package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel

sealed class EventToGameScreenViewModel: EventToViewModel.UserEvent() {

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

    sealed class EventWithoutSettingLoadingStateBeforeProcessing(
        override val eventMode: Event.EventMode = Event.EventMode.Normal(
            setLoadingStateBeforeProcessing = false,
            pushToHead = false,
        )
    ): EventToGameScreenViewModel() {
        object RemoveFlaggedBombs : EventWithoutSettingLoadingStateBeforeProcessing()

        object RemoveOpenedSlices : EventWithoutSettingLoadingStateBeforeProcessing()

        object ToggleFlagging : EventWithoutSettingLoadingStateBeforeProcessing()
    }

    object CloseGameStatusDialog: EventToGameScreenViewModel()
}
