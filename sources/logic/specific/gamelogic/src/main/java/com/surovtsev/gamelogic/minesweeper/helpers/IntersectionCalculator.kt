package com.surovtsev.gamelogic.minesweeper.helpers

import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.core.models.gles.pointer.Pointer
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.models.game.cellpointers.PointedCellWithSpaceBorder
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.InnerNode
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Leaf
import com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Node
import com.surovtsev.gamestateholder.GameStateHolder
import logcat.logcat
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

@GameScope
class IntersectionCalculator @Inject constructor(
    private val pointer: Pointer,
    private val gameStateHolder: GameStateHolder,
) {

    fun getCell(): PointedCell? {
        val gameState = gameStateHolder.gameStateFlow.value ?: return null

        val cubeInfo = gameState.cubeInfo

        val cubeSpaceBorder = cubeInfo.cubeSpaceBorder
        val borders = cubeSpaceBorder.cells
        val squaredCubeSphereRadius = cubeSpaceBorder.squaredCellSphereRadius

        val pointerDescriptor = pointer.getPointerDescriptor()

        val candidateCubes =
            mutableListOf<Pair<Float, PointedCellWithSpaceBorder>>()


        var intersectionCalculationCount = 0
        var projectionCalculationCount = 0

        cubeInfo.cubeSkin.skinsWithIndexes.forEach { (skin, cellIndex) ->
            do {
                if (skin.isEmpty()) {
                    continue
                }

                val spaceParameter = cellIndex.getValue(borders)
                val center = spaceParameter.center

                projectionCalculationCount++
                val projection = pointerDescriptor.calcProjection(center)
                val squaredDistance = (center - projection).length2()


                if (squaredDistance <= squaredCubeSphereRadius) {
                    val fromNear = (pointerDescriptor.near - projection).length()

                    candidateCubes.add(
                        fromNear to PointedCellWithSpaceBorder(
                            cellIndex, skin, spaceParameter
                        )
                    )
                }
            } while (false)
        }

        val sortedCandidates = candidateCubes.sortedBy { it.first }

        for (c in sortedCandidates) {
            val candidate = c.second

            intersectionCalculationCount++
            if (candidate.spaceBorder.testIntersection(pointerDescriptor)) {

                logcat { "old: intersectionCalculationCount: $intersectionCalculationCount; projectionCalculationCount: $projectionCalculationCount" }
                return candidate
            }
        }

        logcat { "alt: intersectionCalculationCount: $intersectionCalculationCount; projectionCalculationCount: $projectionCalculationCount" }
        return null
    }

    fun getCellAlt(): PointedCell? {
        val gameState = gameStateHolder.gameStateFlow.value ?: return null

        val cubeInfo = gameState.cubeInfo

        val nodesToTest = LinkedList<Node>()
        when (val root = cubeInfo.aabbTree.root) {
            is InnerNode -> nodesToTest.addAll(root.children.first)
            is Leaf -> nodesToTest.add(root)
            else -> throw IllegalArgumentException("wrong type of aabb tree node")
        }

        val pointerDescriptor = pointer.getPointerDescriptor()

        val candidateCubes = LinkedList<Pair<Float, PointedCellWithSpaceBorder>>()

        val squaredCubeSphereRadius = cubeInfo.cubeSpaceBorder.squaredCellSphereRadius
        val skins = cubeInfo.cubeSkin.skins

        var intersectionCalculationCount = 0
        var projectionCalculationCount = 0

        do {
//            logcat { "nodesToTest.count: ${nodesToTest.count()}" }

            val currNode = nodesToTest.poll() ?: break

            val spaceBorder = when(currNode) {
                is Leaf -> currNode.cellSpaceBorder
                is InnerNode -> currNode.children.third
                else -> throw IllegalArgumentException("wrong type")
            }

            intersectionCalculationCount++
            val isIntersects = spaceBorder.testIntersection(pointerDescriptor)

//            logcat { "currNode.spaceBorder: $spaceBorder; isIntersects: $isIntersects" }

            if (isIntersects) {
                when (currNode) {
                    is Leaf -> {
                        do {
                            val cellIndex = currNode.cellIndex
                            val skin = cellIndex.getValue(skins)
                            val cellSpaceBorder = currNode.cellSpaceBorder

                            if (skin.isEmpty()) {
                                break
                            }

                            projectionCalculationCount++
                            val center = cellSpaceBorder.center
                            val projection = pointerDescriptor.calcProjection(center)
                            val squaredDistance = (center - projection).length2()

                            if (squaredDistance <= squaredCubeSphereRadius) {
                                val fromNear = (pointerDescriptor.near - projection).length()

                                candidateCubes.add(
                                    fromNear to PointedCellWithSpaceBorder(
                                        cellIndex, skin, cellSpaceBorder
                                    )
                                )
                            }
                        } while (false)
                    }
                    is InnerNode -> nodesToTest.addAll(currNode.children.first)
                }
            }
        } while (true)

        val sortedCandidates = candidateCubes.sortedBy { it.first }

        logcat { "sortedCandidates: $sortedCandidates" }
        logcat { "alt: intersectionCalculationCount: $intersectionCalculationCount; projectionCalculationCount: $projectionCalculationCount" }

        return sortedCandidates.getOrNull(0)?.second
    }
}