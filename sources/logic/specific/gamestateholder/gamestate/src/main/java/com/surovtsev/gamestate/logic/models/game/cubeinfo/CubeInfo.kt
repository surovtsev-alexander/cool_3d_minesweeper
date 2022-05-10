package com.surovtsev.gamestate.logic.models.game.cubeinfo

import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.helpers.gamelogic.NeighboursCalculator
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import com.surovtsev.gamestate.logic.dagger.GameStateScope
import com.surovtsev.gamestate.logic.models.game.aabb.tree.AABBTree
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.InnerNode
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Leaf
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Node
import com.surovtsev.gamestate.logic.models.game.aabb.treealt.AABBTreeAlt
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.CubeSpaceBorder
import logcat.logcat
import java.util.LinkedList
import javax.inject.Inject
import kotlin.math.max

@GameStateScope
data class CubeInfo @Inject constructor(
    val cubeCoordinates: CubeCoordinates,
    val cubeSkin: CubeSkin,
    val cubeSpaceBorder: CubeSpaceBorder,
    val neighboursCalculator: NeighboursCalculator,
    val aabbTreeAlt: AABBTreeAlt,

    val gameConfig: GameConfig,
)
