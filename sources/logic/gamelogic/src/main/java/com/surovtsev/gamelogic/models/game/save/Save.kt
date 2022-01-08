package com.surovtsev.gamelogic.models.game.save

import com.surovtsev.gamestate.GameState
import com.surovtsev.gamestate.models.game.config.GameConfig
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
            asyncTimeSpan: AsyncTimeSpan,
        ): Save {
            return Save(
                gameState.gameConfig,
                CameraInfoToSave.createObject(gameState.cameraInfo),
                GameLogicToSave.createObject(gameState, asyncTimeSpan),
                CubeSkinToSave.createObject(gameState.cubeInfo.cubeSkin)
            )
        }
    }
}