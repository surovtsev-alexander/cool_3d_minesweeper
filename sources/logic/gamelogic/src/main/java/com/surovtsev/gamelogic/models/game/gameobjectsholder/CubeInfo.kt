package com.surovtsev.gamelogic.models.game.gameobjectsholder

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.gamelogic.models.game.skin.cube.CubeSkin
import javax.inject.Inject

@GameScope
class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin
)
