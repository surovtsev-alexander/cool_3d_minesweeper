package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameObject
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.GLCube
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameTouchHandler
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellPosition
import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.models.game.cube.Cube
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCellWithSpaceParameters
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.IPointer

interface ICanUpdateTexture {
    fun updateTexture(pointedCell: PointedCell)
}

class GLCubes(
    context: Context,
    val cubeViewHelper: CubeViewHelper,
    val gameObject: GameObject,
    val gameStatusesReceiver: IGameStatusesReceiver,
    val cube: Cube
):
    ICanUpdateTexture
{

    val glObject =
        GLCube(
            context, cubeViewHelper.triangleCoordinates,
            cubeViewHelper.isEmpty,
            cubeViewHelper.textureCoordinates
        )

    val gameTouchHandler =
        GameTouchHandler(
            gameObject,
            this,
            gameStatusesReceiver
        )

    override fun updateTexture(pointedCell: PointedCell) {
        val position = pointedCell.position
        val description = pointedCell.description
        val id = position.id
        val empty = description.isEmpty()

        if (empty) {
            val cubeIndexsCount = CubesCoordinatesGenerator.invExtendedIndexedArray.size
            val startPos = cubeIndexsCount * id

            glObject.isEmptyArray.updateBuffer(
                FloatArray(cubeIndexsCount) { 1f },
                startPos,
                cubeIndexsCount
            )
        } else {
            val textureIndexesCount =
                TextureCoordinatesHelper.textureToSquareTemplateCoordinates.count()
            val startPos = textureIndexesCount * id * 6

            val xx = arrayOf(
                description.texture[1],
                description.texture[2],
                description.texture[0],
                description.texture[2],
                description.texture[0],
                description.texture[1],
            )


            val resArray = xx.map {
                TextureCoordinatesHelper.textureCoordinates[it]!!.asIterable()
            }.flatten().toFloatArray()
            glObject.textureCoordinatesArray.updateBuffer(
                resArray,
                startPos,
                resArray.count()
            )
        }
    }

    private val counts = cube.counts
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