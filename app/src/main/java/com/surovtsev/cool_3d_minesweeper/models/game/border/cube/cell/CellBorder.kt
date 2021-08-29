package com.surovtsev.cool_3d_minesweeper.models.game.border.cube.cell

import com.surovtsev.cool_3d_minesweeper.models.game.border.cube.cell.edge.EdgeBorder
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.PointerDescriptor
import glm_.vec3.Vec3

class CellBorder(
    val center: Vec3,
    halfSpace: Vec3
) {
    val up: EdgeBorder
    val down: EdgeBorder
    val left: EdgeBorder
    val right: EdgeBorder
    val near: EdgeBorder
    val far: EdgeBorder

    val edges: Array<EdgeBorder>

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
            EdgeBorder(
                A,
                B,
                C,
                D
            )
        down =
            EdgeBorder(
                E,
                F,
                G,
                H
            )
        left =
            EdgeBorder(
                A,
                B,
                F,
                E
            )
        right =
            EdgeBorder(
                D,
                C,
                G,
                H
            )
        near =
            EdgeBorder(
                A,
                D,
                H,
                E
            )
        far =
            EdgeBorder(
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