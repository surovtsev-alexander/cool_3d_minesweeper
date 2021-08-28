package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.CubeDescription
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameTouchHandler
import com.surovtsev.cool_3d_minesweeper.models.game.CubePosition
import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.PointedCube
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.collision.CubeSpaceParameters
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.ClickHelper
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.IPointer

interface ICanUpdateTexture {
    fun updateTexture(pointedCube: PointedCube)
}

class GLCubes(context: Context, val cubes: Cubes): ICanUpdateTexture {

    val glObject = ModelObject(context, cubes.triangleCoordinates,
        cubes.isEmpty,
        cubes.textureCoordinates)

    val gameTouchHandler =
        GameTouchHandler(
            cubes.gameObject,
            this,
            cubes.gameStatusesReceiver
        )

    override fun updateTexture(pointedCube: PointedCube) {
        val position = pointedCube.position
        val description = pointedCube.description
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

    class PointedCubeWithSpaceParameters(
        position: CubePosition,
        description: CubeDescription,
        val spaceParameters: CubeSpaceParameters
    ): PointedCube(position, description) {

    }

    val gameObject = cubes.gameObject
    val collisionCubes = cubes.collisionCubes
    val counts = collisionCubes.counts
    val descriptions = gameObject.descriptions
    val spaceParameters = collisionCubes.spaceParameters
    val squaredCubeSphereRadius = collisionCubes.squaredCubeSphereRadius

    fun testPointer(pointer: IPointer, clickType: ClickHelper.ClickType, currTime: Long): Unit {
        val pointerDescriptor = pointer.getPointerDescriptor()

        var candidateCubes =
            mutableListOf<Pair<Float, PointedCubeWithSpaceParameters>>()

        gameObject.iterateCubes { p: CubePosition ->
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
                        fromNear to PointedCubeWithSpaceParameters(
                            p, description, spaceParameter
                        )
                    )
                }
            } while (false)
        }

        val sortedCandidates = candidateCubes.sortedBy { it.first }

        for (c in sortedCandidates) {
            val candidate = c.second

            if (candidate.spaceParameters.testIntersection(pointerDescriptor)) {
                gameTouchHandler.touch(clickType, candidate, currTime)
                break
            }
        }
    }
}