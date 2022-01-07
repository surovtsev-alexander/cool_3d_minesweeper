package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamelogic.models.game.gamestatus.GameStatus
import com.surovtsev.gamelogic.models.game.gamestatus.GameStatusHelper
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@GameScope
class GameStatusHolder @Inject constructor(
    private val asyncTimeSpan: AsyncTimeSpan,
    private val gameNotPausedFlow: GameNotPausedFlow,
    subscriptionsHolder: SubscriptionsHolder,
): Subscription {
    private val _gameStatusWithElapsedFlow = MutableStateFlow(
        GameStatusWithElapsed()
    )
    val gameStatusWithElapsedFlow: GameStatusWithElapsedFlow = _gameStatusWithElapsedFlow.asStateFlow()

    private val _bombsLeftFlow = MutableStateFlow(0)
    val bombsLeftFlow: BombsLeftFlow = _bombsLeftFlow.asStateFlow()

    fun gameStatus() = gameStatusWithElapsedFlow.value.gameStatus

    fun isGameNotStarted() = (gameStatus() == GameStatus.NoBombsPlaced)

    fun isGameInProgress() = GameStatusHelper.isGameInProgress(gameStatus())

    fun isGameOver() = GameStatusHelper.isGameOver(gameStatus())

    init {
        asyncTimeSpan.flush()

        _gameStatusWithElapsedFlow.value = GameStatusWithElapsed(
            GameStatus.NoBombsPlaced,
            asyncTimeSpan.getElapsed()
        )

        subscriptionsHolder.addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {

            gameNotPausedFlow.zip(
                gameStatusWithElapsedFlow
            ) { gameNotPaused: Boolean, (gameStatus: GameStatus, _) ->

                gameNotPaused and GameStatusHelper.isGameInProgress(gameStatus)

            }.stateIn(customCoroutineScope).collectLatest { turnOn ->
                if (turnOn) {
                    asyncTimeSpan.turnOn()
                } else {
                    asyncTimeSpan.turnOff()
                }
            }

        }
    }

    fun setGameStatus(newStatus: GameStatus) {
        _gameStatusWithElapsedFlow.value = GameStatusWithElapsed(
            newStatus,
            asyncTimeSpan.getElapsed()
        )

        if (isGameInProgress()) {
            asyncTimeSpan.turnOn()
        } else if (isGameOver()) {
            asyncTimeSpan.turnOff()
        }
    }

    fun applySavedData(elapsedTime: Long, gameStatus: GameStatus) {
        asyncTimeSpan.setElapsed(elapsedTime)
        setGameStatus(gameStatus)
    }

    fun setBombsLeft(v: Int) {
        _bombsLeftFlow.value = v
    }

    fun decBombsLeft() {
        _bombsLeftFlow.value -= 1
    }

    fun testIfWin() {
        if (_bombsLeftFlow.value == 0) {
            setGameStatus(GameStatus.Win)
        }
    }
}
