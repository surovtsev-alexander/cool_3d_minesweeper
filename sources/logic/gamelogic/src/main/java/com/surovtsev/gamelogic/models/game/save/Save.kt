package com.surovtsev.gamelogic.models.game.save

import com.surovtsev.gamelogic.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamelogic.models.game.config.GameConfig
import com.surovtsev.gamelogic.models.game.save.helpers.CameraInfoToSave
import com.surovtsev.gamelogic.models.game.save.helpers.CubeSkinToSave
import com.surovtsev.gamelogic.models.game.save.helpers.GameLogicToSave
import com.surovtsev.gamelogic.models.game.skin.cube.CubeSkin
import com.surovtsev.utils.math.camerainfo.CameraInfo
import com.surovtsev.utils.timers.async.AsyncTimeSpan

class Save(
    val gameConfig: GameConfig,
    val cameraInfoToSave: CameraInfoToSave,
    val gameLogicToSave: GameLogicToSave,
    val cubeSkinToSave: CubeSkinToSave,
) {
    companion object {
        fun createObject(
            gameConfig: GameConfig,
            cameraInfo: CameraInfo,
            gameLogic: GameLogic,
            cubeSkin: CubeSkin,
            asyncTimeSpan: AsyncTimeSpan,
        ): Save {
            return Save(
                gameConfig,
                CameraInfoToSave.createObject(cameraInfo),
                GameLogicToSave.createObject(gameLogic, asyncTimeSpan),
                CubeSkinToSave.createObject(cubeSkin)
            )
        }
    }
}