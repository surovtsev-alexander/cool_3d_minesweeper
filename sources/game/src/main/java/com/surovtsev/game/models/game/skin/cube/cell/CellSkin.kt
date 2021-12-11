package com.surovtsev.game.models.game.skin.cube.cell

import com.surovtsev.game.minesweeper.scene.texturecoordinateshelper.TextureCoordinatesHelper
import com.surovtsev.game.minesweeper.scene.texturecoordinateshelper.TextureCoordinatesHelper.TextureType
import glm_.vec3.Vec3i


class CellSkin(
    var isBomb: Boolean = false,
    var neighbourBombs: Vec3i = Vec3i(),
    var texture: Array<TextureType> = Array(3) { TextureType.CLOSED }
) {
    private fun isTexture(t: TextureType) = texture[0] == t

    fun isEmpty() = isTexture(TextureType.EMPTY)

    fun isClosed() = isTexture(TextureType.CLOSED)

    fun isMarked() = isTexture(TextureType.MARKED)

    fun isOpenedNumber() = !isMarked() && !isClosed() && !isEmpty()

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

    private fun isZero(): Boolean {
        for (t in texture)
            if (t != TextureType.ZERO) return false

        return true
    }

    fun emptyIfZero() {
        if (!isZero()) return
        setTexture(TextureType.EMPTY)
    }

    override fun toString() = "$isBomb $neighbourBombs"

    fun getTextureCoordinates() = TextureCoordinatesHelper.getTextureCoordinates(texture)
}
