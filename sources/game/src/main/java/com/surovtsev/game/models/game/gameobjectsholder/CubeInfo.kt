package com.surovtsev.game.models.game.gameobjectsholder

import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.game.models.game.skin.cube.CubeSkin
import javax.inject.Inject

@GameScope
class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin
)
