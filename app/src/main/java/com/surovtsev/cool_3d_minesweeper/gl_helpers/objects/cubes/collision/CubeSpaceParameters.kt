package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.Pointer
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.PointerDescriptor
import glm_.vec3.Vec3
import glm_.vec4.Vec4

class CubeSpaceParameters(
    val center: Vec3,
    halfDims: Vec3
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

        fun testIntersection(pointerDescriptor: PointerDescriptor): Boolean {
            return true
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
        val p1 = center - halfDims
        val p2 = center + halfDims

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