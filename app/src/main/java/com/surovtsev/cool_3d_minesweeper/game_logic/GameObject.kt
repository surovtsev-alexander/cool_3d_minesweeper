package com.surovtsev.cool_3d_minesweeper.game_logic

import glm_.vec3.Vec3s

data class GameObject(
    val counts: Vec3s
) {
    val descriptions: Array<Array<Array<CubeDescription>>> =
        Array(counts.x.toInt()) {
            Array(counts.y.toInt()) {
                Array(counts.z.toInt()) {
                    CubeDescription()
                }
            }
        }
}
