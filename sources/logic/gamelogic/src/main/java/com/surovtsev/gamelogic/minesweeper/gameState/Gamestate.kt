package com.surovtsev.gamelogic.minesweeper.gameState

import com.surovtsev.gamelogic.models.game.config.GameConfig
import com.surovtsev.gamelogic.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.utils.math.camerainfo.CameraInfo

class GameState(
    val gameConfig: GameConfig,
    val cubeInfo: CubeInfo,
    val cameraInfo: CameraInfo,
)