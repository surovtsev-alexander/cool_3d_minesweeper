package com.surovtsev.gamescreen.models.game.gameobjectsholder

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.gamescreen.models.game.skin.cube.CubeSkin
import javax.inject.Inject

@GameScope
class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin
)
