package com.surovtsev.gamelogic.minesweeper.gameState

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.models.game.cellpointers.CellIndex
import com.surovtsev.gamelogic.models.game.config.GameConfig
import com.surovtsev.gamelogic.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.utils.math.camerainfo.CameraInfo
import javax.inject.Inject

@GameScope
class GameState @Inject constructor(
    val gameConfig: GameConfig,
    val cubeInfo: CubeInfo,
    val cameraInfo: CameraInfo,
) {
    val cubesToOpen = mutableListOf<CellIndex>()
    val cubesToRemove = mutableListOf<CellIndex>()

    fun applySavedData(
        cubesToOpen_: List<CellIndex>,
        cubesToRemove_: List<CellIndex>) {
        cubesToOpen += cubesToOpen_
        cubesToRemove += cubesToRemove_
    }
}