package com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell

import com.surovtsev.core.models.gles.pointer.PointerDescriptor
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.cell.edge.EdgeSpaceBorder
import glm_.vec3.Vec3
import logcat.logcat

class CellSpaceBorder(
    leftDownNearPoint: Vec3,
    rightUpFarPoint: Vec3,
    val center: Vec3 = (leftDownNearPoint + rightUpFarPoint) / 2,
) {
    override fun toString(): String {
        return "${mainDiagonalPoints[0]}; ${mainDiagonalPoints[1]}"
    }

    companion object {
        fun createByCenterAndHalfSpace(
            center: Vec3,
            halfSpace: Vec3
        ): CellSpaceBorder {
            assert(halfSpace.x > 0)
            assert(halfSpace.y > 0)
            assert(halfSpace.z > 0)

            logcat { "center: $center; halfSpace: $halfSpace" }

            return CellSpaceBorder(
                center - halfSpace,
                center + halfSpace,
                center,
            )
        }
    }

    val mainDiagonalPoints: List<Vec3> = listOf(
        leftDownNearPoint,
        rightUpFarPoint
    )

    init {
        logcat { this.toString() }
    }

    private val edgeSpaces: Array<EdgeSpaceBorder>

    init {
        val (x1, y1, z1) = leftDownNearPoint
        val (x2, y2, z2) = rightUpFarPoint

        val a = Vec3(x1, y2, z1)
        val b = Vec3(x1, y2, z2)
        val c = Vec3(x2, y2, z2)
        val d = Vec3(x2, y2, z1)

        val e = Vec3(x1, y1, z1)
        val f = Vec3(x1, y1, z2)
        val g = Vec3(x2, y1, z2)
        val h = Vec3(x2, y1, z1)

        val up =
            EdgeSpaceBorder(
                a,
                b,
                c,
                d
            )
        val down =
            EdgeSpaceBorder(
                e,
                f,
                g,
                h
            )
        val left =
            EdgeSpaceBorder(
                a,
                b,
                f,
                e
            )
        val right =
            EdgeSpaceBorder(
                d,
                c,
                g,
                h
            )
        val near =
            EdgeSpaceBorder(
                a,
                d,
                h,
                e
            )
        val far =
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
