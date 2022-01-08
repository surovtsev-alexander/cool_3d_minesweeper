package com.surovtsev.gamestate

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusHolder
import com.surovtsev.gamestate.models.game.spaceborders.cube.CubeSpaceBorder
import com.surovtsev.utils.math.camerainfo.CameraInfo

class GameState(
    val gameConfig: GameConfig,
    val cubeInfo: CubeInfo,
    val cameraInfo: CameraInfo,
    val gameStatusHolder: GameStatusHolder,
    val cubeSpaceBorder: CubeSpaceBorder,
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