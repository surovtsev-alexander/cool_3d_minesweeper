package com.surovtsev.gamescreen.minesweeper.gameState

import com.surovtsev.gamescreen.models.game.config.GameConfig
import com.surovtsev.gamescreen.models.game.gameobjectsholder.CubeInfo

class GameState(
    val gameConfig: GameConfig,
    val cubeInfo: CubeInfo,
    val cameraInfo: CubeInfo,
)