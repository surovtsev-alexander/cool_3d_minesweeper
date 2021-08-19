package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import glm_.vec3.Vec3
import glm_.vec3.Vec3s

class CollisionCubes(
    val counts: Vec3s,
    val cubeSphereRadius: Float,
    centers: Array<Vec3>,
    halfDims: Vec3
) {
    val spaceParameters: Array<Array<Array<CubeSpaceParameters>>>

    val squaredCubeSphereRadius = cubeSphereRadius * cubeSphereRadius

    companion object {
        fun calcId(counts: Vec3s, x: Int, y: Int, z: Int) =
            x + counts.x * (y + counts.y * z)
    }

    init {
        spaceParameters = Array(counts.x.toInt()) { x ->
            Array(counts.y.toInt()) { y ->
                Array(counts.z.toInt()) { z ->
                    CubeSpaceParameters(centers[calcId(counts, x, y, z)], halfDims)
                }
            }
        }
    }
}

