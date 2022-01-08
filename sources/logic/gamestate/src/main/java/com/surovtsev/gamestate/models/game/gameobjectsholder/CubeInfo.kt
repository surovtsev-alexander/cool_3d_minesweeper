package com.surovtsev.gamestate.models.game.gameobjectsholder

import com.surovtsev.gamestate.dagger.GameScope
import com.surovtsev.gamestate.helpers.CubeCoordinates
import com.surovtsev.gamestate.models.game.skin.cube.CubeSkin
import javax.inject.Inject

@GameScope
class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin
)
