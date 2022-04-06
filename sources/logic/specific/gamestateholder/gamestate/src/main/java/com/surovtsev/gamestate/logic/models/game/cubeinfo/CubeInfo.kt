package com.surovtsev.gamestate.logic.models.game.cubeinfo

import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import com.surovtsev.gamestate.logic.dagger.GameStateScope
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.CubeSpaceBorder
import javax.inject.Inject

@GameStateScope
class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin,
    val cubeSpaceBorder: CubeSpaceBorder,
)
