package com.surovtsev.cool_3d_minesweeper.math

import kotlin.math.sqrt

data class Point3d<T>(val x: T, val y: T, val z: T) {
    companion object {
        fun divideShort(a: Point3d<Float>, b: Point3d<Short>) =
            Point3d<Float>(a.x / b.x, a.y / b.y, a.z / b.z)

        fun divideFloat(a: Point3d<Float>, b: Point3d<Float>) =
            Point3d<Float>(a.x / b.x, a.y / b.y, a.z / b.z)

        fun divide(a: Point3d<Float>, b: Int) =
            Point3d<Float>(a.x / b, a.y / b, a.z / b)

        fun multiply(a: Point3d<Float>, b: Int) =
            Point3d<Float>(a.x * b, a.y * b, a.z * b)

        fun multiply(a: Point3d<Float>, b: Point3d<Int>) =
            Point3d<Float>(a.x * b.x, a.y * b.y, a.z * b.z)

        fun minus(a: Point3d<Float>, b: Point3d<Float>) =
            Point3d<Float>(a.x - b.x, a.y - b.y, a.z - b.z)

        fun plus(a: Point3d<Float>, b: Point3d<Float>) =
            Point3d<Float>(a.x + b.x, a.y + b.y, a.z + b.z)

        fun len(a: Point3d<Float>) =
            sqrt(a.x * a.x + a.y * a.y + a.z * a.z)
    }
}
