package com.surovtsev.cool_3d_minesweeper.models.game.border.cube.cell.edge

import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.PointerDescriptor
import com.surovtsev.cool_3d_minesweeper.utils.math.MyMath
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.sqrt

data class EdgeBorder(
    val p1: Vec3,
    val p2: Vec3,
    val p3: Vec3,
    val p4: Vec3
) {
    val plane: Vec4
    val space: Float

    init {
        val (ax, ay, az) = p1
        val (bx, by, bz) = p2
        val (cx, cy, cz) = p3

        val A = (by - ay) * cz + (az - bz) * cy + ay * bz - az * by
        val B = (ax - bx) * cz + (bz - az) * cx - ax * bz + az * bx
        val C = (bx - ax) * cy + (ay - by) * cx + ax * by - ay * bx
        val D = (ay * bx - ax * by) * cz + (ax * bz - az * bx) * cy + (az * by - ay * bz) * cx

        plane = Vec4(A, B, C, D)

        space = (p1 - p2).length() * (p1 - p4).length()
    }

    companion object {
        fun dot(a: Vec4, b: Vec4): Float {
            return a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3]
        }

        fun dist(p1: Vec3, p2: Vec3) = (p1 - p2).length()

        fun space(p1: Vec3, p2: Vec3, p3: Vec3): Float {
            val a =
                dist(
                    p1,
                    p2
                )
            val b =
                dist(
                    p2,
                    p3
                )
            val c =
                dist(
                    p3,
                    p1
                )

            val p = (a + b + c) / 2

            return sqrt(
                (p * (p - a) * (p - b) * (p - c)).toDouble()
            ).toFloat()
        }
    }

    private fun calcLinePlaneIntersection(pointerDescriptor: PointerDescriptor): Vec3? {
        val x1 = pointerDescriptor.near
        val n = pointerDescriptor.n

        val denominator =
            dot(
                Vec4(n, 0),
                plane
            )
        val numerator =
            dot(
                Vec4(x1, 1),
                plane
            )

        if (MyMath.isZero(denominator)) {
            return null
        } else {
            val res = x1 - n * numerator / denominator

            return res
        }
    }

    private fun isIn(p: Vec3): Boolean {
        val ss = space(
            p1,
            p2,
            p
        ) + space(
            p2,
            p3,
            p
        ) + space(
            p3,
            p4,
            p
        ) + space(
            p4,
            p1,
            p
        )

        return MyMath.isZero(ss - space)
    }

    fun testIntersection(pointerDescriptor: PointerDescriptor): Boolean {
        val p = calcLinePlaneIntersection(pointerDescriptor)

        if (p == null) {
            return false
        } else {
            return isIn(p)
        }
    }
}
