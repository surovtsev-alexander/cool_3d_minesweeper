package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper.TextureType
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper.numberTextures
import glm_.vec3.Vec3i


data class CubeDescription(
    val isBomb: Boolean = false,
    var neighbourBombs: Vec3i = Vec3i(),
    var texture: TextureType =TextureType.CLOSED
) {
    fun isEmpty() = texture == TextureType.EMPTY

    fun touch() {
        when (texture) {
            TextureType.CLOSED -> {
               if (isBomb) {
                   texture = TextureType.EXPLODED_BOMB
               } else {
                   texture = numberTextures[2]
               }
            }
            TextureType.MARKED -> {
                if (isBomb) {
                    texture = TextureType.EMPTY
                } else {
                    texture = TextureType.EXPLODED_BOMB
                }
            }
            else -> {
                texture = TextureType.EMPTY
            }
        }
    }
}
