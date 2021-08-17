package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import android.content.Context
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CollisionCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision.CubeDescription
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.IPointer
import com.surovtsev.cool_3d_minesweeper.utils.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.utils.TextureHelper
import glm_.vec3.Vec3i

class GLCubes(context: Context, val cubes: Cubes) {

    val glObject = ModelObject(context, cubes.triangleCoordinates,
        cubes.trianglesNums, cubes.trianglesTextures,
        cubes.textureCoordinates)

    fun updateTexture(id: Int, description: CubeDescription) {
        if (false && LoggerConfig.LOG_GL_CUBES) {
            Log.d("TEST", "buffer_len: ${glObject.texturesArray.floatBuffer.capacity()}")
            Log.d("TEST", "id: $id")
        }

        run {
            val cubeIndexsCount = CubesCoordinatesGenerator.invExtendedIndexedArray.size
            val startPos = cubeIndexsCount * id
            val c = description.getColor() + 0.1f

            glObject.texturesArray.updateBuffer(
                FloatArray(cubeIndexsCount) { c },
                startPos,
                cubeIndexsCount
            )
        }

        run {
            val textureIndexesCount = TextureCoordinatesHelper.textureToSquareTemplateCoordinates.count()
            val startPos = textureIndexesCount * id * 6

            val resArray = (0 until 6).map {
                TextureCoordinatesHelper.textureCoordinates[TextureCoordinatesHelper.TextureTypes.EXPLODED_BOMB]!!.asIterable()
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
        val centers = collisionCubes.centers
        val squaredCubeSphereRadius = collisionCubes.squaredCubeSphereRadius

        val x1 = pointer.near
        val x2 = pointer.far
        val n = x2 - x1

        val projectionCalculator = pointer.getProjectionCalculator()

        if (false) {
            val testId = Vec3i(0, counts.y - 1, counts.z - 1)
            val d = descriptions[testId.x][testId.y][testId.z]
            d.isOpened = true
            updateTexture(
                CollisionCubes.calcId(counts, testId.x, testId.y, testId.z),
                d
            )
        }

        /*
        val sb = StringBuilder()

        if (LoggerConfig.LOG_GL_CUBES) {
            sb.append("_\nx1: $x1 x2: $x2 n: ${x2 - x1} squaredCubeSphereRadius: ${squaredCubeSphereRadius}\n")
        }
        */

        //var candidateCubes

        for (x in 0 until counts.x) {
            for (y in 0 until counts.y) {
                for (z in 0 until counts.z) {
                    val id = CollisionCubes.calcId(counts, x, y, z)
                    val d = descriptions[x][y][z]

                    if (d.isOpened) {
                        continue
                    }

                    val c = centers[x][y][z]

                    val projection = projectionCalculator(c)

                    val squaredDistance = (c - projection).length2()

                    /*
                    if (LoggerConfig.LOG_GL_CUBES) {
                        sb.append("c: $c projection $projection squaredDistance: ${squaredDistance}\n")
                    }
                     */

                    if (squaredDistance <= squaredCubeSphereRadius) {
                        d.isOpened = true

                        updateTexture(id, d)
                    }
                }
            }
        }

        /*
        if (LoggerConfig.LOG_GL_CUBES) {
            Log.d("TEST", sb.toString())
        }
         */
    }
}