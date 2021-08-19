package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CollisionCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CubeDescription
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CubeSpaceParameters
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.IPointer

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
                description.texture,
                description.texture,
                description.texture,
                description.texture,
                description.texture,
                description.texture,
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

    fun testPointer(pointer: IPointer): Unit {
        val gameObject = cubes.gameObject
        val collisionCubes = gameObject.collisionCubes
        val counts = collisionCubes.counts
        val descriptions = gameObject.descriptions
        val spaceParameters = collisionCubes.spaceParameters
        val squaredCubeSphereRadius = collisionCubes.squaredCubeSphereRadius

        val pointerDescriptor = pointer.getPointerDescriptor()

        data class PointedCube(
            val id: Int,
            val spaceParameters: CubeSpaceParameters,
            val description: CubeDescription)

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
                candidate.description.touch()
                updateTexture(candidate.id, candidate.description)
                break
            }
        }
    }
}