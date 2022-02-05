package com.surovtsev.gamestate.logic.models.game.save

import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.logic.models.game.save.helpers.CameraInfoToSave
import com.surovtsev.gamestate.logic.models.game.save.helpers.CubeSkinToSave
import com.surovtsev.gamestate.logic.models.game.save.helpers.GameLogicToSave
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