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


package com.surovtsev.gamelogic.minesweeper.interaction.eventhandler

import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.scene.SceneCalculator
import com.surovtsev.gamestate.logic.models.game.save.Save
import com.surovtsev.gamestateholder.GameStateHolder
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
            is EventToMinesweeper.NewGame            -> suspend { newGame(false) }
            is EventToMinesweeper.LoadGame           -> suspend { newGame(true) }
            is EventToMinesweeper.SaveGame           -> ::saveGame
            is EventToMinesweeper.SetGameStateToNull -> ::setGameStateToNull
            is EventToMinesweeper.Tick               -> ::tick
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
        gameStateHolder.newGame(tryToLoad)
    }

    private suspend fun setGameStateToNull(
    ) {
        gameStateHolder.setGameStateToNull()
    }

    private suspend fun saveGame() {
        val gameState = gameStateHolder.gameStateFlow.value ?: return

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