package com.surovtsev.gamescreen.models.game.save

import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamescreen.models.game.camerainfo.CameraInfo
import com.surovtsev.gamescreen.models.game.config.GameConfig
import com.surovtsev.gamescreen.models.game.save.helpers.CameraInfoToSave
import com.surovtsev.gamescreen.models.game.save.helpers.CubeSkinToSave
import com.surovtsev.gamescreen.models.game.save.helpers.GameLogicToSave
import com.surovtsev.gamescreen.models.game.skin.cube.CubeSkin
import com.surovtsev.utils.timers.TimeSpan

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
            timeSpan: TimeSpan,
        ): Save {
            return Save(
                gameConfig,
                CameraInfoToSave.createObject(cameraInfo),
                GameLogicToSave.createObject(gameLogic, timeSpan),
                CubeSkinToSave.createObject(cubeSkin)
            )
        }
    }
}