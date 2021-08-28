package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.collision

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.PointerDescriptor
import com.surovtsev.cool_3d_minesweeper.utils.math.MyMath
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.sqrt

class CubeSpaceParameters(
    val center: Vec3,
    halfSpace: Vec3
) {
    data class Edge(
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
                return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w
            }

            fun dist(p1: Vec3, p2: Vec3) = (p1 - p2).length()

            fun space(p1: Vec3, p2: Vec3, p3: Vec3): Float {
                val a = dist(p1, p2)
                val b = dist(p2, p3)
                val c = dist(p3, p1)

                val p = (a + b + c) / 2

                return sqrt(
                    (p * (p - a) * (p - b) * (p - c)).toDouble()
                ).toFloat()
            }
        }

        private fun calcLinePlaneIntersection(pointerDescriptor: PointerDescriptor): Vec3? {
            val x1 = pointerDescriptor.near
            val n = pointerDescriptor.n

            val plane3 = Vec3(plane)

            val denominator = dot(Vec4(n, 0), plane)
            val numerator = dot(Vec4(x1, 1), plane)

            if (MyMath.isZero(denominator)) {
                return null
            } else {
                val res = x1 - n * numerator / denominator

                if (false) {
                    Log.d("TEST", "denominator $denominator numerator $numerator")
                    Log.d("TEST", "plane $plane x1 $x1 x2 ${pointerDescriptor.far} n $n res $res")
                    Log.d("TEST", "---")
                }

                return res
            }
        }

        private fun isIn(p: Vec3): Boolean {
            val ss = space(p1, p2, p) + space(p2, p3, p) + space(p3, p4, p) + space(p4, p1, p)

            if (false) {
                Log.d("TEST", "SS $ss space $space")
            }

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

    val up: Edge
    val down: Edge
    val left: Edge
    val right: Edge
    val near: Edge
    val far: Edge

    val edges: Array<Edge>

    init {
        val p1 = center - halfSpace
        val p2 = center + halfSpace

        val (x1, y1, z1) = p1
        val (x2, y2, z2) = p2

        val A = Vec3(x1, y2, z1)
        val B = Vec3(x1, y2, z2)
        val C = Vec3(x2, y2, z2)
        val D = Vec3(x2, y2, z1)

        val E = Vec3(x1, y1, z1)
        val F = Vec3(x1, y1, z2)
        val G = Vec3(x2, y1, z2)
        val H = Vec3(x2, y1, z1)

        up = Edge(A, B, C, D)
        down = Edge(E, F, G, H)
        left = Edge(A, B, F, E)
        right = Edge(D, C, G, H)
        near = Edge(A, D, H, E)
        far = Edge (B, C, G, F)

        edges = arrayOf(
            up, down, left, right, near, far
        )
    }

    fun testIntersection(pointerDescriptor: PointerDescriptor): Boolean {
        return (edges.firstOrNull { it.testIntersection(pointerDescriptor) }) != null
    }
}