package com.surovtsev.gamestate.logic.models.game.gameobjectsholder

import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import com.surovtsev.gamestate.logic.dagger.GameStateScope
import javax.inject.Inject

@GameStateScope
class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin
)