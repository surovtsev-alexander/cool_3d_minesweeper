package com.surovtsev.cool_3d_minesweeper.game_logic

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper.TextureType
import glm_.vec3.Vec3i


data class CubeDescription(
    var isBomb: Boolean = false,
    var neighbourBombs: Vec3i = Vec3i(),
    var texture: Array<TextureType> = Array<TextureType>(3) { TextureType.CLOSED }
) {
    fun isEmpty() = texture[0] == TextureType.EMPTY

    fun isClosed() = texture[0] == TextureType.CLOSED

    fun setTexture(tt: TextureType) {
        for (i in 0 until texture.count()) {
            texture[i] = tt
        }
    }

    fun setNumbers() {
        assert(!isBomb)
        for (i in 0 until 3) {
            texture[i] = TextureCoordinatesHelper.numberTextures[neighbourBombs[i]]
        }
    }
}
