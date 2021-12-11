package com.surovtsev.game.models.game.save

import com.surovtsev.game.minesweeper.gamelogic.GameLogic
import com.surovtsev.game.models.game.camerainfo.CameraInfo
import com.surovtsev.game.models.game.config.GameConfig
import com.surovtsev.game.models.game.save.helpers.CameraInfoToSave
import com.surovtsev.game.models.game.save.helpers.CubeSkinToSave
import com.surovtsev.game.models.game.save.helpers.GameLogicToSave
import com.surovtsev.game.models.game.skin.cube.CubeSkin

class Save(
    val gameConfig: GameConfig,
    val cameraInfoToSave: CameraInfoToSave,
    val gameLogicToSave: GameLogicToSave,
    val cubeSkinToSave: CubeSkinToSave
) {
    companion object {
        fun createObject(
            gameConfig: GameConfig,
            cameraInfo: CameraInfo,
            gameLogic: GameLogic,
            cubeSkin: CubeSkin
        ): Save {
            return Save(
                gameConfig,
                CameraInfoToSave.createObject(cameraInfo),
                GameLogicToSave.createObject(gameLogic),
                CubeSkinToSave.createObject(cubeSkin)
            )
        }
    }
}