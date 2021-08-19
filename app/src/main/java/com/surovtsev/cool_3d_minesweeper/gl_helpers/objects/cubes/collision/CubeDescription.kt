package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.collision

import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper.TextureType
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper.numberTextures
import glm_.vec3.Vec3
import glm_.vec3.Vec3i


data class CubeDescription(
    val isBomb: Boolean = false,
    var neighbourBombs: Vec3i = Vec3i(),
    var texture: Array<TextureType> = Array<TextureType>(3) { TextureType.CLOSED }
) {
    fun isEmpty() = texture[0] == TextureType.EMPTY

    fun touch() {
        when (texture[0]) {
            TextureType.CLOSED -> {
               if (isBomb) {
                    setTexture(TextureType.EXPLODED_BOMB)
               } else {
                   neighbourBombs = Vec3i(0, 2, 7)

                   for (i in 0 until 3) {
                       texture[i] = numberTextures[neighbourBombs[i]]
                   }
               }
            }
            TextureType.MARKED -> {
                if (isBomb) {
                    setTexture(TextureType.EMPTY)
                } else {
                    setTexture(TextureType.EXPLODED_BOMB)
                }
            }
            else -> {
                setTexture(TextureType.EMPTY)
            }
        }
    }

    private fun setTexture(tt: TextureType) {
        for (i in 0 until texture.count()) {
            texture[i] = tt
        }
    }
}
