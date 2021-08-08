package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import glm_.vec3.Vec3
import glm_.vec3.Vec3s

class CollisionCubes(
    val counts: Vec3s,
    val cubeSphereRadius: Float
) {
    val centers: Array<Array<Array<Vec3>>> = Array(counts.x.toInt()) {
        Array(counts.y.toInt()) {
            Array(counts.z.toInt()) {
                Vec3()
            }
        }
    }

    val squaredCubeSphereRadius = cubeSphereRadius * cubeSphereRadius

    companion object {
        fun calcId(counts: Vec3s, x: Int, y: Int, z: Int) =
            x + counts.x * (y + counts.y * z)
    }

    constructor(
        counts_: Vec3s,
        cubeSphereRadius_: Float,
        centers_: Array<Vec3>): this(
        counts_, cubeSphereRadius_
    ) {
        for (x in 0 until counts.x) {
            for (y in 0 until counts.y) {
                for (z in 0 until counts.z) {
                    val id = calcId(counts, x, y, z)

                    centers[x][y][z] = centers_[id]
                }
            }
        }
    }
}

