package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.collision

import com.surovtsev.cool_3d_minesweeper.models.game.CubePosition
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

class CollisionCubes(
    val counts: Vec3s,
    val cubeSphereRadius: Float,
    centers: Array<Vec3>,
    halfSpace: Vec3
) {
    val spaceParameters: Array<Array<Array<CubeSpaceParameters>>>

    val squaredCubeSphereRadius = cubeSphereRadius * cubeSphereRadius

    init {
        spaceParameters = Array(counts.x.toInt()) { x ->
            Array(counts.y.toInt()) { y ->
                Array(counts.z.toInt()) { z ->
                    CubeSpaceParameters(centers[CubePosition.calcId(counts, x, y, z)], halfSpace)
                }
            }
        }
    }
}
