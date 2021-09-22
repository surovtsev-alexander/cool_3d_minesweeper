package com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder

import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.border.cube.CubeBorder
import javax.inject.Inject

@GameControllerScope
class GameObjectsHolder @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeBorder: CubeBorder,
    val cubeSkin: CubeSkin
)
