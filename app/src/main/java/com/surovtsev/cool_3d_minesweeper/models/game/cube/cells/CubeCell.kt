package com.surovtsev.cool_3d_minesweeper.models.game.cube.cells

import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.edge.Edge
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.PointerDescriptor
import glm_.vec3.Vec3

class CubeCell(
    val center: Vec3,
    halfSpace: Vec3
) {
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

        up =
            Edge(
                A,
                B,
                C,
                D
            )
        down =
            Edge(
                E,
                F,
                G,
                H
            )
        left =
            Edge(
                A,
                B,
                F,
                E
            )
        right =
            Edge(
                D,
                C,
                G,
                H
            )
        near =
            Edge(
                A,
                D,
                H,
                E
            )
        far =
            Edge(
                B,
                C,
                G,
                F
            )

        edges = arrayOf(
            up, down, left, right, near, far
        )
    }

    fun testIntersection(pointerDescriptor: PointerDescriptor): Boolean {
        return (edges.firstOrNull { it.testIntersection(pointerDescriptor) }) != null
    }
}