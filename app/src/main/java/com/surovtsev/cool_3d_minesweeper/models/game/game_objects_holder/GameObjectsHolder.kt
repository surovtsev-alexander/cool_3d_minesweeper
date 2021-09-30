package com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameScope
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import javax.inject.Inject

@GameScope
class GameObjectsHolder @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin
)
