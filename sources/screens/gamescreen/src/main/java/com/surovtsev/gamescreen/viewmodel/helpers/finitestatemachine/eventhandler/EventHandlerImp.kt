package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.eventhandler

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toNormalPriorityEventProcessor
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.finitestatemachine.state.toIdle
import com.surovtsev.gamelogic.minesweeper.interaction.eventhandler.EventToMinesweeper
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import logcat.logcat
import javax.inject.Inject

@GameScreenScope
class EventHandlerImp @Inject constructor(
    private val eventHandlerParameters: EventHandlerParameters,
): EventHandler {

    override val transitions: List<EventHandler.Transition> = emptyList()

    override fun handleEvent(
        event: Event,
        state: State
    ): EventHandlingResult {
        val eventProcessorAction = when (event) {
            is EventToViewModel.HandleScreenLeaving                      -> suspend { handleScreenLeaving(event.owner) }
            is EventToViewModel.Init                                     -> suspend { newGame(true) }
            is EventToGameScreenViewModel.NewGame                        -> suspend { newGame(false) }
            is EventToGameScreenViewModel.OpenGameMenuAndSetLoadingState -> suspend { openGameMenu(setLoadingState = true) }
            is EventToGameScreenViewModel.OpenGameMenuAndSetIdleState    -> suspend { openGameMenu(setLoadingState = false) }
            is EventToGameScreenViewModel.SetIdleState                   -> ::setIdleState
            is EventToGameScreenViewModel.CloseGameMenu                  -> suspend { closeGameMenu() }
            is EventToGameScreenViewModel.GoToMainMenu                   -> ::goToMainMenu
            is EventToGameScreenViewModel.EventWithoutSettingLoadingStateBeforeProcessing.RemoveFlaggedBombs
                                                                         -> ::removeFlaggedBombs
            is EventToGameScreenViewModel.EventWithoutSettingLoadingStateBeforeProcessing.RemoveOpenedSlices
                                                                         -> ::removeOpenedSlices
            is EventToGameScreenViewModel.EventWithoutSettingLoadingStateBeforeProcessing.ToggleFlagging
                                                                         -> ::toggleFlagging
            is EventToGameScreenViewModel.CloseGameStatusDialog          -> ::closeGameStatusDialog
            else                                                         -> null
        }

        return EventHandlingResult.GeneratorHelper.processOrSkipIfNull(
            eventProcessorAction.toNormalPriorityEventProcessor()
        )
    }

    enum class Errors(val message: String) {
        MAIN_MENU_IS_NOT_OPENED("main menu is not opened"),
        GAME_IS_NOT_IN_PROGRESS("game is not in progress"),
        GAME_IS_IN_PROGRESS("game is in progress"),
        CAN_NOT_OPEN_MENU_TWICE_SEQUENTIALLY("can not open menu twice sequentially"),
        CRITICAL_ERROR_RELOADING("critical error. reloading"),
        INTERNAL_SETTINGS_SCREEN_ERROR_001("internal settings screen error");
    }


    private var gameControlsImp: GameControlsImp? = null
    private var uiGameControlsMutableFlows: UIGameControlsMutableFlows? = null
    private var uiGameControlsFlows: UIGameControlsFlows? = null

    private suspend fun handleScreenLeaving(
        @Suppress("UNUSED_PARAMETER") owner: LifecycleOwner
    ): EventProcessingResult {
        with(eventHandlerParameters) {
            restartableCoroutineScopeComponent
                .subscriberImp
                .stop()

            gLESRenderer
                .openGLEventsHandler = null

            gameComponent
                .minesweeper
                .eventHandler
                .handleEventWithBlocking(
                    EventToMinesweeper.SetGameStateToNull
                )
        }
        logcat { "handleScreenLeaving" }

        return EventProcessingResult.Ok()
    }

    private suspend fun newGame(
        loadGame: Boolean
    ): EventProcessingResult {
        val state = eventHandlerParameters.fsmStateFlow.value
        val gameScreeData = state.data as? GameScreenData

        if (gameScreeData is GameScreenData.GameMenu) {
            return unstackState(
                gameScreeData,
                newEventToPush = if (loadGame) {
                    EventToGameScreenViewModel.LoadGame()
                } else {
                    EventToGameScreenViewModel.NewGame
                }
            )
        }

        val newState = with(eventHandlerParameters) {
            timeSpanComponent
                .manuallyUpdatableTimeAfterDeviceStartupFlowHolder
                .tick()

            gLESRenderer
                .openGLEventsHandler = null

            gameComponent.let { gC ->
                gameControlsImp = gC.gameControlsImp

                uiGameControlsMutableFlows = gC.uiGameControlsMutableFlows
                uiGameControlsFlows = gC.uiGameControlsFlows

            touchListenerComponent.touchListener.bindHandlers(
                    gC.touchHandlerImp,
                    gC.moveHandlerImp
                )
            }

            gameComponent.minesweeper.eventHandler.handleEventWithBlocking(
                if (loadGame) {
                    EventToMinesweeper.LoadGame
                } else {
                    EventToMinesweeper.NewGame
                }
            )

            gLESRenderer.openGLEventsHandler =
                gameComponent.minesweeper.openGLEventsHandler


            setFlagging(loadGame)

            state.toIdle(
                GameScreenData.GameInProgress(
                    uiGameControlsFlows!!
                )
            )
        }
        return EventProcessingResult.Ok(
            newState = newState
        )
    }

    private suspend fun openGameMenu(
        setLoadingState: Boolean = false
    ): EventProcessingResult {
        val state = eventHandlerParameters.fsmStateFlow.value
        val gameScreenData = state.data as? GameScreenData

        return if (gameScreenData == null) {
            EventProcessingResult.Error(
                Errors.INTERNAL_SETTINGS_SCREEN_ERROR_001.message
            )
        } else if (gameScreenData is GameScreenData.GameMenu) {
            EventProcessingResult.Error(
                Errors.CAN_NOT_OPEN_MENU_TWICE_SEQUENTIALLY.message
            )
        } else {
            val newScreenData = GameScreenData.GameMenu(
                gameScreenData
            )

            val newDescription = if (setLoadingState) {
                Description.Loading
            } else {
                Description.Idle
            }

            val newState = State(
                newDescription,
                newScreenData,
            )
            EventProcessingResult.Ok(
                newState = newState
            )
        }
    }

    private suspend fun setIdleState(
    ): EventProcessingResult {
        return EventProcessingResult.Ok(
            newState = eventHandlerParameters.fsmStateFlow.value.toIdle()
        )
    }

    private suspend fun closeGameMenu(
        silent: Boolean = false
    ): EventProcessingResult {
        val state = eventHandlerParameters.fsmStateFlow.value
        val gameScreenData = state.data as? GameScreenData

        return if (gameScreenData == null) {
            return EventProcessingResult.Error(
                Errors.INTERNAL_SETTINGS_SCREEN_ERROR_001.message
            )
        } else if (gameScreenData is GameScreenData.GameMenu) {
            unstackState(gameScreenData)
        } else {
            if (silent) {
                EventProcessingResult.Ok()
            } else {
                EventProcessingResult.Error(
                    Errors.MAIN_MENU_IS_NOT_OPENED.message
                )
            }
        }
    }

    private suspend fun goToMainMenu(
    ): EventProcessingResult {
        closeGameMenu(true)
        return EventProcessingResult.Ok(
            EventToViewModel.Finish
        )
    }

    private suspend fun removeFlaggedBombs(
    ): EventProcessingResult {
        return skipIfGameIsNotInProgress {
            gameControlsImp?.removeFlaggedCells = true
        }
    }

    private suspend fun removeOpenedSlices(
    ): EventProcessingResult {
        return skipIfGameIsNotInProgress {
            gameControlsImp?.removeOpenedSlices = true
        }
    }

    private suspend fun toggleFlagging(
    ): EventProcessingResult {
        return skipIfGameIsNotInProgress {
            gameControlsImp?.let {
                setFlagging(
                    !it.flagging
                )
            }
        }
    }

    private fun setFlagging(
        newVal: Boolean
    ) {
        gameControlsImp?.flagging = newVal
        uiGameControlsMutableFlows?.flagging?.value = newVal
    }

    private suspend fun closeGameStatusDialog(
    ): EventProcessingResult {
        uiGameControlsMutableFlows?.uiGameStatus?.value = UIGameStatus.Unimportant
        return EventProcessingResult.Ok()
    }

    private suspend fun unstackState(
        gameScreenData: GameScreenData,
        newEventToPush: Event? = null,
    ): EventProcessingResult {
        val prevData = (gameScreenData as? GameScreenData.HasPrevData)?.prevData

        val state = eventHandlerParameters.fsmStateFlow.value

        return if (prevData == null) {
            EventProcessingResult.Error(
                Errors.CRITICAL_ERROR_RELOADING.message,
            )
        } else {
            EventProcessingResult.Ok(
                newEventToPush,
                state.toIdle(
                    prevData
                )
            )
        }
    }

    private suspend fun skipIfGameIsNotInProgress(
        action: suspend (gameInProgress: GameScreenData.GameInProgress) -> Unit
    ): EventProcessingResult {
        val state = eventHandlerParameters.fsmStateFlow.value
        val gameScreenData = state.data as? GameScreenData

        if (gameScreenData is GameScreenData.GameInProgress) {
            action(gameScreenData)
        }
        return EventProcessingResult.Ok()
    }
}