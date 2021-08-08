package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.IPointer

data class GameObject(
    val collisionCubes: CollisionCubes
) {
    val descriptions: Array<Array<Array<CubeDescription>>> =
        Array(collisionCubes.counts.x.toInt()) {
            Array(collisionCubes.counts.y.toInt()) {
                Array(collisionCubes.counts.z.toInt()) {
                    CubeDescription()
                }
            }
        }
}