package com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder

import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.border.cube.CubeBorder

class GameObjectsHolder(
    gameConfig: GameConfig
) {
    val cubeCoordinates: CubeCoordinates = CubeCoordinates.createObject(
        gameConfig
    )
    val cubeBorder: CubeBorder = CubeBorder(
        gameConfig,
        cubeCoordinates.centers
    )
    val cubeSkin: CubeSkin = CubeSkin(
        gameConfig
    )
}
