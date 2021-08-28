package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes

import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

data class CubesCoordinatesGeneratorConfig(
    val counts: Vec3s,
    val dimensions: Vec3,
    val gaps: Vec3,
    val bombsRate: Float,
    val gameStatusesReceiver: IGameStatusesReceiver
) {
    init {
        assert(bombsRate > 0)
        assert(bombsRate < 1)
    }
}
