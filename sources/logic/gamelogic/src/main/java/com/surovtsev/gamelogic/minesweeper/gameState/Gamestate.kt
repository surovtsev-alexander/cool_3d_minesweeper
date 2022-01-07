package com.surovtsev.gamelogic.minesweeper.gameState

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.models.game.config.GameConfig
import com.surovtsev.gamelogic.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.utils.math.camerainfo.CameraInfo
import javax.inject.Inject

@GameScope
class GameState @Inject constructor(
    val gameConfig: GameConfig,
    val cubeInfo: CubeInfo,
    val cameraInfo: CameraInfo,
)