package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper.TextureType
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper.numberTextures
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHelper
import glm_.vec3.Vec3
import glm_.vec3.Vec3i


data class CubeDescription(
    val isBomb: Boolean = false,
    var neighbourBombs: Vec3i = Vec3i(),
    var texture: Array<TextureType> = Array<TextureType>(3) { TextureType.CLOSED }
) {
    fun isEmpty() = texture[0] == TextureType.EMPTY

    fun setTexture(tt: TextureType) {
        for (i in 0 until texture.count()) {
            texture[i] = tt
        }
    }
}
