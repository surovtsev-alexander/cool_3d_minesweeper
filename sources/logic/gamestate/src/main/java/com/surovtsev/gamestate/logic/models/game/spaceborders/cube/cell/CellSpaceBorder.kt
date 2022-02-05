package com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell

import com.surovtsev.core.models.gles.pointer.PointerDescriptor
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.edge.EdgeSpaceBorder
import glm_.vec3.Vec3

class CellSpaceBorder(
    val center: Vec3,
    halfSpace: Vec3
) {
    private val up: EdgeSpaceBorder
    private val down: EdgeSpaceBorder
    private val left: EdgeSpaceBorder
    private val right: EdgeSpaceBorder
    private val near: EdgeSpaceBorder
    private val far: EdgeSpaceBorder

    private val edgeSpaces: Array<EdgeSpaceBorder>

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
            EdgeSpaceBorder(
                a,
                b,
                c,
                d
            )
        down =
            EdgeSpaceBorder(
                e,
                f,
                g,
                h
            )
        left =
            EdgeSpaceBorder(
                a,
                b,
                f,
                e
            )
        right =
            EdgeSpaceBorder(
                d,
                c,
                g,
                h
            )
        near =
            EdgeSpaceBorder(
                a,
                d,
                h,
                e
            )
        far =
            EdgeSpaceBorder(
                b,
                c,
                g,
                f
            )

        edgeSpaces = arrayOf(
            up, down, left, right, near, far
        )
    }

    fun testIntersection(pointerDescriptor: PointerDescriptor): Boolean {
        return (edgeSpaces.firstOrNull { it.testIntersection(pointerDescriptor) }) != null
    }
}