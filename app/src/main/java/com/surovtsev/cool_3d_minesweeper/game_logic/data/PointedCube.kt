package com.surovtsev.cool_3d_minesweeper.game_logic.data

import com.surovtsev.cool_3d_minesweeper.game_logic.CubeDescription
import com.surovtsev.cool_3d_minesweeper.game_logic.GameObject

open class PointedCube(
    val position: CubePosition,
    val description: CubeDescription
) {
    override fun toString() = "${position.getVec()} $description"
}
