package com.surovtsev.cool3dminesweeper.models.game.gameobjectsholder

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.skin.cube.CubeSkin
import javax.inject.Inject

@GameScope
class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin
)
