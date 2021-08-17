package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import glm_.vec3.Vec3i

data class CubeDescription(
    val isBomb: Boolean = false,
    val isMarked: Boolean = false,
    var isOpened: Boolean = false,
    var neighbourBombs: Vec3i = Vec3i()
) {
    val openedSides: Array<Boolean> = Array<Boolean>(6) { false }

    fun getColor(): Int {
        if (!isOpened) {
            return 0
        } else {
            return 1
        }
    }
}
