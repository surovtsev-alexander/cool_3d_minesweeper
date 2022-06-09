/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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
