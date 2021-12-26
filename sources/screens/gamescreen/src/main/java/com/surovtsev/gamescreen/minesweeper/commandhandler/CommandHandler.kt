package com.surovtsev.gamescreen.minesweeper.commandhandler

import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamescreen.minesweeper.scene.SceneCalculator
import com.surovtsev.gamescreen.models.game.camerainfo.CameraInfo
import com.surovtsev.gamescreen.models.game.config.GameConfig
import com.surovtsev.gamescreen.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamescreen.models.game.save.Save
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@GameScope
class CommandHandler @Inject constructor(
    private val sceneCalculator: SceneCalculator,
    private val saveController: SaveController,
    private val gameConfig: GameConfig,
    private val cameraInfo: CameraInfo,
    private val gameLogic: GameLogic,
    private val cubeInfo: CubeInfo,
    private val asyncTimeSpan: AsyncTimeSpan,
) {
    private val mutex = Mutex()

    suspend fun handleCommand(
        command: CommandToMinesweeper
    ) {
        val action = when (command) {
            is CommandToMinesweeper.NewGame     -> ::newGame
            is CommandToMinesweeper.LoadGame    -> ::loadGame
            is CommandToMinesweeper.SaveGame    -> ::saveGame
            is CommandToMinesweeper.Pause       -> ::pause
            is CommandToMinesweeper.Resume      -> ::resume
            is CommandToMinesweeper.Tick        -> ::tick
        }

        mutex.withLock {
            action.invoke()
        }
    }

    private suspend fun newGame() {

    }

    private suspend fun loadGame() {

    }

    private suspend fun saveGame() {
        if (!gameLogic.gameLogicStateHelper.isGameInProgress()) {
            return
        }

        val save = Save.createObject(
            gameConfig,
            cameraInfo,
            gameLogic,
            cubeInfo.cubeSkin,
            asyncTimeSpan
        )
        saveController.save(
            SaveTypes.SaveGameJson,
            save
        )
    }

    private suspend fun pause() {

    }

    private suspend fun resume() {

    }

    private suspend fun tick() {
        sceneCalculator.nextIteration()
    }
}