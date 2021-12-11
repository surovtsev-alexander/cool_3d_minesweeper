package com.surovtsev.cool3dminesweeper.models.game.border.cube.cell

import com.surovtsev.cool3dminesweeper.models.game.border.cube.cell.edge.EdgeBorder
import com.surovtsev.game.utils.gles.model.pointer.PointerDescriptor
import glm_.vec3.Vec3

class CellBorder(
    val center: Vec3,
    halfSpace: Vec3
) {
    private val up: EdgeBorder
    private val down: EdgeBorder
    private val left: EdgeBorder
    private val right: EdgeBorder
    private val near: EdgeBorder
    private val far: EdgeBorder

    private val edges: Array<EdgeBorder>

    init {
        val p1 = center - halfSpace
        val p2 = center + halfSpace

        val (x1, y1, z1) = p1
        val (x2, y2, z2) = p2

        val a = Vec3(x1, y2, z1)
        val b = Vec3(x1, y2, z2)
        val c = Vec3(x2, y2, z2)
        val d = Vec3(x2, y2, z1)

        val e = Vec3(x1, y1, z1)
        val f = Vec3(x1, y1, z2)
        val g = Vec3(x2, y1, z2)
        val h = Vec3(x2, y1, z1)

        up =
            EdgeBorder(
                a,
                b,
                c,
                d
            )
        down =
            EdgeBorder(
                e,
                f,
                g,
                h
            )
        left =
            EdgeBorder(
                a,
                b,
                f,
                e
            )
        right =
            EdgeBorder(
                d,
                c,
                g,
                h
            )
        near =
            EdgeBorder(
                a,
                d,
                h,
                e
            )
        far =
            EdgeBorder(
                b,
                c,
                g,
                f
            )

        edges = arrayOf(
            up, down, left, right, near, far
        )
    }

    fun testIntersection(pointerDescriptor: PointerDescriptor): Boolean {
        return (edges.firstOrNull { it.testIntersection(pointerDescriptor) }) != null
    }
}