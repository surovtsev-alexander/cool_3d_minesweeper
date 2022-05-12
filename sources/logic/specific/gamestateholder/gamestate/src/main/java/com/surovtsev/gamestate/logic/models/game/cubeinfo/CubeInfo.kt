package com.surovtsev.gamestate.logic.models.game.cubeinfo

import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.helpers.gamelogic.NeighboursCalculator
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import com.surovtsev.gamestate.logic.dagger.GameStateScope
import com.surovtsev.gamestate.logic.models.game.aabb.tree.AABBTree
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.CubeSpaceBorder
import javax.inject.Inject

@GameStateScope
data class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin,
    val cubeSpaceBorder: CubeSpaceBorder,
    val neighboursCalculator: NeighboursCalculator,
    val aabbTree: AABBTree,

    val gameConfig: GameConfig,
)
