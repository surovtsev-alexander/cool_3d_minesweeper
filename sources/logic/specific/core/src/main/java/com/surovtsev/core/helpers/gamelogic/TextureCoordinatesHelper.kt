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


package com.surovtsev.core.helpers.gamelogic

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.utils.math.MyMath
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import glm_.vec3.Vec3i

object TextureCoordinatesHelper {
    val textureToSquareTemplateCoordinates = listOf(
        0f, 1f,
        1f, 0f,
        0f, 0f,

        1f, 1f,
        1f, 0f,
        0f, 1f,
    )

    private const val cols = 3
    private const val rows = 4

    enum class TextureType {
        EMPTY,
        CLOSED,
        FLAGGED,
        EXPLODED_BOMB,
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT
    }

    private const val commonTexturesCount = 4

    val numberTextures = TextureType.values().drop(commonTexturesCount)

    private val commonTexturesPositions = mapOf(
        TextureType.EMPTY to Vec2i(0, 0),
        TextureType.CLOSED to Vec2i(0, 0),
        TextureType.FLAGGED to Vec2i(1, 0),
        TextureType.EXPLODED_BOMB to Vec2i(2, 0)
    )

    private val numberTexturesPositions = numberTextures.mapIndexed { idx, elem ->
        val col = idx % cols
        val row = idx / cols + 1
        elem to Vec2i(col, row)
    }

    private val texturesPositions = commonTexturesPositions + numberTexturesPositions

    private fun getTextureCoordinates(pos: Vec2i): FloatArray {
        @Suppress("SpellCheckingInspection")
        val calcPointCoords = {p: Int, dim: Int ->
            Vec2(
                1f * p / dim,
                1f * (p + 1) / dim
            )
        }
        @Suppress("SpellCheckingInspection")
        val xCoords = calcPointCoords(pos[0],
            cols
        )
        @Suppress("SpellCheckingInspection")
        val yCoords = calcPointCoords(pos[1],
            rows
        )

        @Suppress("SpellCheckingInspection")
        val getCoord = { point: Vec2, flag: Float ->
            if (MyMath.isZero(flag)) point[0] else point[1]
        }

        val ll = textureToSquareTemplateCoordinates.count() / 2

        val res = FloatArray(ll * 2)

        for (i in 0 until ll) {
            res[i * 2] = getCoord(xCoords, textureToSquareTemplateCoordinates[i * 2])
            res[i * 2 + 1] = getCoord(yCoords, textureToSquareTemplateCoordinates[i * 2 + 1])
        }

        return res
    }


    val textureCoordinates: Map<TextureType, FloatArray> = texturesPositions.map { (t, p) ->
        t to getTextureCoordinates(p)
    }.toMap()


    private val textureTypeCount = TextureType.values().count()
    private val counts = Vec3i(textureTypeCount)

    private val possibleSkins: Array<CellIndex>
    private val possibleTextureCoordinates: Map<Int, FloatArray>

    init {
        val numberTexturesRange = (commonTexturesCount until textureTypeCount)

        val c = (0 until commonTexturesCount).map {
            CellIndex(it, it, it, counts)
        }.toTypedArray()

        val n = numberTexturesRange.map { x ->
            numberTexturesRange.map { y ->
                numberTexturesRange.map { z ->
                    CellIndex(x, y, z, counts)
                }
            }.flatten()
        }.flatten().toTypedArray()

        possibleSkins = c + n

        possibleTextureCoordinates = possibleSkins.map {
            val id = it.id
            val s = it.getVec()

            fun textureIndexes() = arrayOf(
                s[1],
                s[2],
                s[0],
                s[2],
                s[0],
                s[1]
            )

            val a = textureIndexes().map {x: Int ->
                textureCoordinates[TextureType.values()[x]]!!.asIterable()
            }.flatten().toFloatArray()
            id to a
        }.toMap()
    }

    fun getTextureCoordinates(t: Array<TextureType>): FloatArray {
        val ci = CellIndex(
            t[0].ordinal, t[1].ordinal, t[2].ordinal,
            counts
        )

        return possibleTextureCoordinates[ci.id]!!
    }
}