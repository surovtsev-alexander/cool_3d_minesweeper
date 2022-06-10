/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.core.models.game.skin.cube.cell

import com.surovtsev.core.helpers.gamelogic.TextureCoordinatesHelper
import com.surovtsev.core.helpers.gamelogic.TextureCoordinatesHelper.TextureType
import glm_.vec3.Vec3i


class CellSkin(
    var isBomb: Boolean = false,
    var neighbourBombs: Vec3i = Vec3i(),
    var texture: Array<TextureType> = Array(3) { TextureType.CLOSED }
) {
    private fun isTexture(t: TextureType) = texture[0] == t

    fun isEmpty() = isTexture(TextureType.EMPTY)

    fun isClosed() = isTexture(TextureType.CLOSED)

    fun isFlagged() = isTexture(TextureType.FLAGGED)

    fun isOpenedNumber() = !isFlagged() && !isClosed() && !isEmpty()

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
