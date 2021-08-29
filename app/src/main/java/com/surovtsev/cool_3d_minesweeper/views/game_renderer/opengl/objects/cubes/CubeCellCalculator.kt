package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.models.game.GameObject
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.GLCube
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameTouchHandler
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellPosition
import com.surovtsev.cool_3d_minesweeper.models.game.cube.Cube
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCellWithSpaceParameters
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.IPointer


class CubeCellCalculator(
    context: Context,
    val cubeViewHelper: CubeViewHelper,
    val gameObject: GameObject,
    val gameTouchHandler: GameTouchHandler,
    val cube: Cube
) {


    private val descriptions = gameObject.descriptions
    private val spaceParameters = cube.cells
    private val squaredCubeSphereRadius = cube.squaredCubeSphereRadius

    fun testPointer(pointer: IPointer, currTime: Long): Unit {
        val pointerDescriptor = pointer.getPointerDescriptor()

        var candidateCubes =
            mutableListOf<Pair<Float, PointedCellWithSpaceParameters>>()

        gameObject.iterateCubes { p: CellPosition ->
            do {
                val description = p.getValue(descriptions)

                if (description.isEmpty()) {
                    continue
                }

                val spaceParameter = p.getValue(spaceParameters);
                val center = spaceParameter.center

                val projection = pointerDescriptor.calcProjection(center)
                val squaredDistance = (center - projection).length2()


                if (squaredDistance <= squaredCubeSphereRadius) {
                    val fromNear = (pointerDescriptor.near - projection).length()

                    candidateCubes.add(
                        fromNear to PointedCellWithSpaceParameters(
                            p, description, spaceParameter
                        )
                    )
                }
            } while (false)
        }

        val sortedCandidates = candidateCubes.sortedBy { it.first }

        for (c in sortedCandidates) {
            val candidate = c.second

            if (candidate.cell.testIntersection(pointerDescriptor)) {
                gameTouchHandler.touch(pointer.touchType, candidate, currTime)
                break
            }
        }
    }
}