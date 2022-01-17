package com.surovtsev.gamelogic.minesweeper.interaction.eventhandler

import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.camerainfohelperholder.CameraInfoHelperHolder
import com.surovtsev.gamelogic.minesweeper.gamestateholder.GameStateHolder
import com.surovtsev.gamelogic.minesweeper.scene.SceneCalculator
import com.surovtsev.gamestate.models.game.save.Save
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@GameScope
class EventHandler @Inject constructor(
    private val sceneCalculator: SceneCalculator,
    private val saveController: SaveController,
    private val gameStateHolder: GameStateHolder,
    private val asyncTimeSpan: AsyncTimeSpan,
    private val cameraInfoHelperHolder: CameraInfoHelperHolder,
) {
    private val mutex = Mutex()

    fun handleEventWithBlocking(
        event: EventToMinesweeper
    ) {
        runBlocking {
            handleEvent(
                event
            )
        }
    }

    suspend fun handleEvent(
        event: EventToMinesweeper
    ) {
        val action = when (event) {
            is EventToMinesweeper.NewGame     -> suspend { newGame(false) }
            is EventToMinesweeper.LoadGame    -> suspend { newGame(true) }
            is EventToMinesweeper.SaveGame    -> ::saveGame
            is EventToMinesweeper.Tick        -> ::tick
        }

        if (mutex.isLocked && event is EventToMinesweeper.CanBeSkipped) {
            return
        }

        mutex.withLock {
            action.invoke()
        }
    }

    private suspend fun newGame(
        tryToLoad: Boolean = false
    ) {
        cameraInfoHelperHolder.cameraInfoHelperFlow.value.also {
            if (!tryToLoad) {
                it.cameraInfo.moveToOrigin()
            }
            it.update()
        }
        gameStateHolder.newGame(tryToLoad)
    }

    private suspend fun saveGame() {
        val gameState = gameStateHolder.gameStateFlow.value

        if (!gameState.gameStatusHolder.isGameInProgress()) {
            return
        }

        val save = Save.createObject(
            gameState,
            asyncTimeSpan
        )
        saveController.save(
            SaveTypes.SaveGameJson,
            save
        )
    }

    private suspend fun tick() {
        sceneCalculator.nextIteration()
    }
}