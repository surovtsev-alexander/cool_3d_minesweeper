package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.eventhandler

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.toNormalPriorityEventProcessor
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.gamelogic.minesweeper.interaction.eventhandler.EventToMinesweeper
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData
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
        val stateHolder = eventHandlerParameters.stateHolder
        val gameScreeData = stateHolder.data as? GameScreenData

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

            stateHolder.toIdleState(
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
        val stateHolder = eventHandlerParameters.stateHolder
        val gameScreenData = stateHolder.data as? GameScreenData

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

            val newState = StateHolder.createState(
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
            newState = eventHandlerParameters.stateHolder.toIdleState()
        )
    }

    private suspend fun closeGameMenu(
        silent: Boolean = false
    ): EventProcessingResult {
        val stateHolder = eventHandlerParameters.stateHolder
        val gameScreenData = stateHolder.data as? GameScreenData

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
        skipIfGameIsNotInProgress {
            gameControlsImp?.removeFlaggedCells = true
        }
        return EventProcessingResult.Ok()
    }

    private suspend fun removeOpenedSlices(
    ): EventProcessingResult {
        skipIfGameIsNotInProgress {
            gameControlsImp?.removeOpenedSlices = true
        }
        return EventProcessingResult.Ok()
    }

    private suspend fun toggleFlagging(
    ): EventProcessingResult {
        skipIfGameIsNotInProgress {
            gameControlsImp?.let {
                setFlagging(
                    !it.flagging
                )
            }
        }
        return EventProcessingResult.Ok()
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

        val stateHolder = eventHandlerParameters.stateHolder

        return if (prevData == null) {
            EventProcessingResult.Error(
                Errors.CRITICAL_ERROR_RELOADING.message,
            )
        } else {
            EventProcessingResult.Ok(
                newEventToPush,
                stateHolder.toIdleState(
                    prevData
                )
            )
        }
    }

    private suspend fun doActionIfDataIsCorrect(
        isDataCorrect: (gameScreeData: GameScreenData) -> Boolean,
        errorMessage: String,
        silent: Boolean = false,
        action: suspend (gameScreenData: GameScreenData) -> Unit
    ) {
        val stateHolder = eventHandlerParameters.stateHolder

        val gameScreenData = stateHolder.data as? GameScreenData

        do {
            val messageToShow = if (gameScreenData == null) {
                Errors.INTERNAL_SETTINGS_SCREEN_ERROR_001.message
            } else if (!isDataCorrect(gameScreenData)) {
                errorMessage
            } else {
                action.invoke(
                    gameScreenData
                )
                break
            }

            if (!silent) {
                stateHolder.let {
                    it.publishNewState(
                        it.toErrorState(
                            messageToShow
                        )
                    )
                }
            }
        } while (false)
    }

    private suspend fun skipIfGameIsNotInProgress(
        action: suspend (gameInProgress: GameScreenData.GameInProgress) -> Unit
    ) {
        val stateHolder = eventHandlerParameters.stateHolder
        val gameScreenData = stateHolder.data as? GameScreenData

        if (gameScreenData is GameScreenData.GameInProgress) {
            action(gameScreenData)
        }
    }
}