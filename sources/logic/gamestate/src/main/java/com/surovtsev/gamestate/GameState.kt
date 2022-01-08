package com.surovtsev.gamestate

import com.surovtsev.gamestate.dagger.GameScope
import com.surovtsev.gamestate.models.game.cellpointers.CellIndex
import com.surovtsev.gamestate.models.game.config.GameConfig
import com.surovtsev.gamestate.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusHolder
import com.surovtsev.utils.math.camerainfo.CameraInfo
import javax.inject.Inject

@GameScope
class GameState @Inject constructor(
    val gameConfig: GameConfig,
    val cubeInfo: CubeInfo,
    val cameraInfo: CameraInfo,
    val gameStatusHolder: GameStatusHolder,
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