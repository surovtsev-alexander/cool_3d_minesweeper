package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.eventhandler

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.viewmodel.EventToViewModel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.data.Data
import com.surovtsev.gamelogic.minesweeper.interaction.eventhandler.EventToMinesweeper
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameStatus
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData
import javax.inject.Inject

@GameScreenScope
class EventHandlerImp @Inject constructor(
    private val eventHandlerParameters: EventHandlerParameters,
): EventHandler {

    override fun handleEvent(
        event: Event,
        state: State
    ): EventHandlingResult {
        val eventProcessor = when (event) {
            is EventToViewModel.HandleScreenLeaving                      -> suspend { handleScreenLeaving(event.owner) }
            is EventToGameScreenViewModel.NewGame                        -> suspend { newGame(false) }
            is EventToGameScreenViewModel.LoadGame                       -> suspend { newGame(true) }
            is EventToGameScreenViewModel.OpenGameMenuAndSetLoadingState -> suspend { openGameMenu(setLoadingState = true) }
            is EventToGameScreenViewModel.OpenGameMenuAndSetIdleState    -> suspend { openGameMenu(setLoadingState = false) }
            is EventToGameScreenViewModel.SetIdleState                   -> ::setIdleState
            is EventToGameScreenViewModel.CloseGameMenu                  -> suspend { closeGameMenu() }
            is EventToGameScreenViewModel.GoToMainMenu                   -> ::goToMainMenu
            is EventToGameScreenViewModel.RemoveFlaggedBombs             -> ::removeFlaggedBombs
            is EventToGameScreenViewModel.RemoveOpenedSlices             -> ::removeOpenedSlices
            is EventToGameScreenViewModel.ToggleFlagging                 -> ::toggleFlagging
            is EventToGameScreenViewModel.CloseGameStatusDialog          -> ::closeGameStatusDialog
            else                                                         -> null
        }

        return EventHandlingResult.Helper.processOrSkipIfNull(
            eventProcessor
        )
    }

    private var gameControlsImp: GameControlsImp? = null
    private var uiGameControlsMutableFlows: UIGameControlsMutableFlows? = null
    private var uiGameControlsFlows: UIGameControlsFlows? = null

    private suspend fun handleScreenLeaving(
        owner: LifecycleOwner
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

        return EventProcessingResult.Ok()
    }

    private suspend fun newGame(
        loadGame: Boolean
    ): EventProcessingResult {
        doActionIfDataIsCorrect(
            { it is GameScreenData.GameMenu },
            "main menu is not opened",
            true
        ) { gameScreenData ->
            tryUnstackState(gameScreenData)
        }

        with(eventHandlerParameters) {
            doActionIfDataIsCorrect(
                { it is GameScreenData.GameInProgress },
                "game is in progress",
                true
            ) {
                stateHolder.publishDefaultInitialState()
            }


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

            stateHolder.publishIdleState(
                GameScreenData.GameInProgress(
                    uiGameControlsFlows!!
                )
            )
        }
        return EventProcessingResult.Ok()
    }

    private suspend fun openGameMenu(
        setLoadingState: Boolean = false
    ): EventProcessingResult {
        doActionIfDataIsCorrect(
            { it !is GameScreenData.GameMenu },
            "can not open menu twice sequentially"
        ) { gameScreenData ->
            val newScreenData = GameScreenData.GameMenu(
                gameScreenData
            )

            val stateHolder = eventHandlerParameters.stateHolder

            if (setLoadingState) {
                stateHolder.publishLoadingState(
                    newScreenData
                )
            } else {
                stateHolder.publishIdleState(
                    newScreenData
                )
            }
        }
        return EventProcessingResult.Ok()
    }

    private suspend fun setIdleState(
    ): EventProcessingResult {
        eventHandlerParameters.stateHolder.publishIdleState()
        return EventProcessingResult.Ok()
    }

    private suspend fun closeGameMenu(
        silent: Boolean = false
    ): EventProcessingResult {
        doActionIfDataIsCorrect(
            { it is GameScreenData.GameMenu },
            "main menu is not opened",
            silent
        ) { gameScreenData ->
            tryUnstackState(gameScreenData)
        }
        return EventProcessingResult.Ok()
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

    private suspend fun tryUnstackState(
        gameScreenData: GameScreenData
    ) {
        val prevData = (gameScreenData as? GameScreenData.HasPrevData)?.prevData

        val stateHolder = eventHandlerParameters.stateHolder

        if (prevData == null) {
            stateHolder.publishErrorState(
                "critical error. reloading",
                Data.NoData,
            )
            return
        }

        stateHolder.publishIdleState(
            prevData
        )
    }

    private suspend fun doActionIfDataIsCorrect(
        isDataCorrect: (gameScreeData: GameScreenData) -> Boolean,
        errorMessage: String,
        silent: Boolean = false,
        action: suspend (gameScreenData: GameScreenData) -> Unit
    ) {
        val stateHolder = eventHandlerParameters.stateHolder

        val gameScreenData = stateHolder.data as? GameScreenData

        // TODO: refactor
        val msg = if (gameScreenData == null) {
            "error 1"
        } else if (!isDataCorrect(gameScreenData)) {
            errorMessage
        } else {
            null
        }

        if (msg != null) {
            if (!silent) {
                stateHolder.publishErrorState(errorMessage)
            }
        } else {
            action.invoke(gameScreenData!!)
        }
    }

    private suspend fun skipIfGameIsNotInProgress(
        action: suspend (gameInProgress: GameScreenData.GameInProgress) -> Unit
    ) {
        doActionIfDataIsChildOf(
            "game is not in progress",
            true,
            action
        )
        setIdleState()
    }

    private suspend inline fun <reified T: GameScreenData> doActionIfDataIsChildOf(
        errorMessage: String,
        silent: Boolean = false,
        noinline action: suspend (gameScreenData: T) -> Unit
    ) {
        doActionIfDataIsCorrect(
            { it is T },
            errorMessage,
            silent = silent,
            { gameScreenData -> action.invoke(gameScreenData as T)  }
        )
    }
}