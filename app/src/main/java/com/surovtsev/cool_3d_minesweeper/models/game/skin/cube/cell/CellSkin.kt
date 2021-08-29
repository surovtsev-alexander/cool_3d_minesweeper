package com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.cell

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper.TextureType
import glm_.vec3.Vec3i


data class CellSkin(
    var isBomb: Boolean = false,
    var neighbourBombs: Vec3i = Vec3i(),
    var texture: Array<TextureType> = Array<TextureType>(3) { TextureType.CLOSED }
) {
    fun isTexture(t: TextureType) = texture[0] == t

    fun isEmpty() = isTexture(TextureType.EMPTY)

    fun isClosed() = isTexture(TextureType.CLOSED)

    fun isMarked() = isTexture(TextureType.MARKED)

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

    fun isZero(): Boolean {
        for (t in texture)
            if (t != TextureType.ZERO) return false

        return true
    }

    fun emptyIfZero() {
        if (!isZero()) return
        setTexture(TextureType.EMPTY)
    }

    override fun toString() = "$isBomb $neighbourBombs"

    fun hasZero(): Boolean {
        for (i in 0 until 3) {
            if (neighbourBombs[i] == 0) {
                return true
            }
        }
        return false
    }
}
