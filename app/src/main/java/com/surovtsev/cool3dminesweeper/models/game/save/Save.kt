package com.surovtsev.cool3dminesweeper.models.game.save

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.GameLogic
import com.surovtsev.cool3dminesweeper.models.game.camerainfo.CameraInfo
import com.surovtsev.cool3dminesweeper.models.game.config.GameConfig
import com.surovtsev.cool3dminesweeper.models.game.save.helpers.CameraInfoToSave
import com.surovtsev.cool3dminesweeper.models.game.save.helpers.CubeSkinToSave
import com.surovtsev.cool3dminesweeper.models.game.save.helpers.GameLogicToSave
import com.surovtsev.cool3dminesweeper.models.game.skin.cube.CubeSkin


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