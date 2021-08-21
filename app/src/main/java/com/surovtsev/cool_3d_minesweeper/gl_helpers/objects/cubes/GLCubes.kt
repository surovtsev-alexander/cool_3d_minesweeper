package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import android.content.Context
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CollisionCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CubeDescription
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CubeSpaceParameters
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHelperComplex
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.IPointer
import glm_.vec3.Vec3i

class GLCubes(context: Context, val cubes: Cubes) {

    val glObject = ModelObject(context, cubes.triangleCoordinates,
        cubes.isEmpty,
        cubes.textureCoordinates)

    fun updateTexture(id: Int, description: CubeDescription) {
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
                description.texture[0],
                description.texture[2],
                description.texture[1],
                description.texture[2],
                description.texture[1],
                description.texture[0],
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

    data class PointedCube(
        val id: Int,
        val spaceParameters: CubeSpaceParameters,
        val description: CubeDescription)

    fun testPointer(pointer: IPointer, clickType: ClickHelper.ClickType, currTime: Long): Unit {
        val gameObject = cubes.gameObject
        val collisionCubes = gameObject.collisionCubes
        val counts = collisionCubes.counts
        val descriptions = gameObject.descriptions
        val spaceParameters = collisionCubes.spaceParameters
        val squaredCubeSphereRadius = collisionCubes.squaredCubeSphereRadius

        val pointerDescriptor = pointer.getPointerDescriptor()

        var candidateCubes =
            mutableListOf<Pair<Float, PointedCube>>()

        for (x in 0 until counts.x) {
            for (y in 0 until counts.y) {
                for (z in 0 until counts.z) {
                    val id = CollisionCubes.calcId(counts, x, y, z)
                    val d = descriptions[x][y][z]

                    if (d.isEmpty()) {
                        continue
                    }

                    val spaceParameter = spaceParameters[x][y][z]
                    val center = spaceParameter.center

                    val projection = pointerDescriptor.calcProjection(center)
                    val squaredDistance = (center - projection).length2()


                    if (squaredDistance <= squaredCubeSphereRadius) {
                        val fromNear = (pointerDescriptor.near - projection).length()

                        candidateCubes.add(fromNear to PointedCube(
                            id, spaceParameter, d))
                    }
                }
            }
        }

        val sortedCandidates = candidateCubes.sortedBy { it.first }


        for (c in sortedCandidates) {
            val candidate = c.second

            if (candidate.spaceParameters.testIntersection(pointerDescriptor)) {
                touch(clickType, candidate, currTime)
                updateTexture(candidate.id, candidate.description)
                break
            }
        }
    }

    private data class PrevClickInfo(var id: Int, var time: Long)
    private val prevClickInfo = PrevClickInfo(-1, 0L)
    private val doubleClickDelay = 200L

    fun touch(clickType: ClickHelper.ClickType, pointedCube: PointedCube, currTime: Long) {
        val id = pointedCube.id
        val description = pointedCube.description
        when (clickType) {
            ClickHelper.ClickType.CLICK -> {
                if (id == prevClickInfo.id && currTime - prevClickInfo.time < doubleClickDelay) {
                    description.setTexture(TextureCoordinatesHelper.TextureType.EMPTY)
                } else {
                    when (description.texture[0]) {
                        TextureCoordinatesHelper.TextureType.CLOSED -> {
                            if (description.isBomb) {
                                description.setTexture(TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                            } else {
                                description.neighbourBombs = Vec3i(0, 2, 7)

                                for (i in 0 until 3) {
                                    description.texture[i] = TextureCoordinatesHelper.numberTextures[description.neighbourBombs[i]]
                                }
                            }
                        }
                    }
                }
            }
            ClickHelper.ClickType.LONG_CLICK -> {
                when (description.texture[0]) {
                    TextureCoordinatesHelper.TextureType.CLOSED -> {
                        description.setTexture(TextureCoordinatesHelper.TextureType.MARKED)
                    }
                    TextureCoordinatesHelper.TextureType.MARKED -> {
                        description.setTexture(TextureCoordinatesHelper.TextureType.CLOSED)
                    }
                }
            }
        }

        prevClickInfo.id = id
        prevClickInfo.time = currTime
    }
}