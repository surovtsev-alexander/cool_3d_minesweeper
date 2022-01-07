package com.surovtsev.gamelogic.models.game.save

import com.surovtsev.gamelogic.minesweeper.gameState.GameState
import com.surovtsev.gamelogic.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamelogic.models.game.config.GameConfig
import com.surovtsev.gamelogic.models.game.save.helpers.CameraInfoToSave
import com.surovtsev.gamelogic.models.game.save.helpers.CubeSkinToSave
import com.surovtsev.gamelogic.models.game.save.helpers.GameLogicToSave
import com.surovtsev.utils.timers.async.AsyncTimeSpan

class Save(
    val gameConfig: GameConfig,
    val cameraInfoToSave: CameraInfoToSave,
    val gameLogicToSave: GameLogicToSave,
    val cubeSkinToSave: CubeSkinToSave,
) {
    companion object {
        fun createObject(
            gameState: GameState,
            gameLogic: GameLogic,
            asyncTimeSpan: AsyncTimeSpan,
        ): Save {
            return Save(
                gameState.gameConfig,
                CameraInfoToSave.createObject(gameState.cameraInfo),
                GameLogicToSave.createObject(gameLogic, asyncTimeSpan),
                CubeSkinToSave.createObject(gameState.cubeInfo.cubeSkin)
            )
        }
    }
}