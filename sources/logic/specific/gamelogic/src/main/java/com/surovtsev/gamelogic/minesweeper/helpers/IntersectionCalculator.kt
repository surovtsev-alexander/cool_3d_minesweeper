/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.minesweeper.helpers

import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.core.models.gles.pointer.Pointer
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.models.game.cellpointers.PointedCellWithSpaceBorder
import com.surovtsev.gamestateholder.GameStateHolder
import java.util.*
import javax.inject.Inject

@GameScope
class IntersectionCalculator @Inject constructor(
    private val pointer: Pointer,
    private val gameStateHolder: GameStateHolder,
) {

    fun getCellOld(): PointedCell? {
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

                return candidate
            }
        }

        return null
    }

    fun getCell(): PointedCell? {
        val gameState = gameStateHolder.gameStateFlow.value ?: return null

        val cubeInfo = gameState.cubeInfo

        val nodesToTest = LinkedList<com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Node>()
        nodesToTest.add(cubeInfo.aabbTree.root)

        val pointerDescriptor = pointer.getPointerDescriptor()

        val candidateCubes = LinkedList<Pair<Float, PointedCellWithSpaceBorder>>()

        val squaredCubeSphereRadius = cubeInfo.cubeSpaceBorder.squaredCellSphereRadius
        val skins = cubeInfo.cubeSkin.skins

        var projectionCalculationCount = 0
        var checkCalculator = 0
        var skipped = 0

        do {

            val currNode = nodesToTest.poll() ?: break

            val spaceBorder = currNode.cellSpaceBorder


            val center1 = spaceBorder.center


            checkCalculator++
            val projection1 = pointerDescriptor.calcProjection(center1)
            val squaredDistance1 = (center1 - projection1).length2()

            if (squaredDistance1 > spaceBorder.squaredSphereRadius) {
                skipped++
                continue
            }

            when (currNode) {
                is com.surovtsev.gamestate.logic.models.game.aabb.tree.node.Leaf -> {
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
                is com.surovtsev.gamestate.logic.models.game.aabb.tree.node.InnerNode -> {
                    nodesToTest.add(currNode.left)
                    nodesToTest.add(currNode.right)
                }
            }
        } while (true)

        val sortedCandidates = candidateCubes.sortedBy { it.first }

        return sortedCandidates.firstOrNull {
            it.second.spaceBorder.testIntersection(pointerDescriptor)
        }?.second
    }
}