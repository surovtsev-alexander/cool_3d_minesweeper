package com.surovtsev.cool3dminesweeper.models.game.game_objects_holder

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.skin.cube.CubeSkin
import javax.inject.Inject

@GameScope
class GameObjectsHolder @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin
)
