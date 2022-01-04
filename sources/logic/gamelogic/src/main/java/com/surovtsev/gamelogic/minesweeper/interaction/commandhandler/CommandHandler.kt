package com.surovtsev.gamelogic.minesweeper.interaction.commandhandler

import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamelogic.minesweeper.scene.SceneCalculator
import com.surovtsev.gamelogic.models.game.config.GameConfig
import com.surovtsev.gamelogic.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamelogic.models.game.save.Save
import com.surovtsev.utils.math.camerainfo.CameraInfo
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.runBlocking
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

    fun handleCommandWithBlocking(
        command: CommandToMinesweeper
    ) {
        runBlocking {
            handleCommand(
                command
            )
        }
    }

    suspend fun handleCommand(
        command: CommandToMinesweeper
    ) {
        val action = when (command) {
            is CommandToMinesweeper.NewGame     -> ::newGame
            is CommandToMinesweeper.LoadGame    -> ::loadGame
            is CommandToMinesweeper.SaveGame    -> ::saveGame
            is CommandToMinesweeper.Tick        -> ::tick
        }

        if (mutex.isLocked && command is CommandToMinesweeper.CanBeSkipped) {
            return
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

    private suspend fun tick() {
        sceneCalculator.nextIteration()
    }
}