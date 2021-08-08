package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import android.graphics.Point
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.RawCubes
import com.surovtsev.cool_3d_minesweeper.math.Point3d
import glm_.vec3.Vec3

class CollisionCubes(
    val counts: Point3d<Short>,
    val cubeSphereRadius: Float
) {
    val centers: Array<Array<Array<Point3d<Float>>>> = Array(counts.x.toInt()) {
        Array(counts.y.toInt()) {
            Array(counts.z.toInt()) {
                Point3d<Float>(0f, 0f, 0f)
            }
        }
    }

    companion object {
        fun calcId(counts: Point3d<Short>, x: Int, y: Int, z: Int) =
            x + counts.x * (y + counts.y * z)
    }

    constructor(
        counts_: Point3d<Short>,
        cubeSphereRadius_: Float,
        centers_: Array<Point3d<Float>>): this(
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

