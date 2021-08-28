package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper

import com.surovtsev.cool_3d_minesweeper.utils.math.MyMath
import glm_.vec2.Vec2
import glm_.vec2.Vec2i

object TextureCoordinatesHelper {
    val textureToSquareTemplateCoordinates = floatArrayOf(
        0f, 1f,
        1f, 0f,
        0f, 0f,

        1f, 1f,
        1f, 0f,
        0f, 1f,
    )

    private val cols = 4
    private val rows = 3

    enum class TextureType {
        EMPTY,
        CLOSED,
        MARKED,
        EXPLODED_BOMB,
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN
    }

    val commonTexturesCount = 4

    val numberTextures = TextureType.values().drop(4)

    val commonTexturesPositions = mapOf(
        TextureType.EMPTY to Vec2i(0, 0),
        TextureType.CLOSED to Vec2i(0, 0),
        TextureType.MARKED to Vec2i(1, 0),
        TextureType.EXPLODED_BOMB to Vec2i(2, 0)
    )

    val numberTexturesPositions = numberTextures.mapIndexed { idx, elem ->
        val col = idx % cols
        val row = idx / cols + 1
        elem to Vec2i(col, row)
    }

    val texturesPositions = commonTexturesPositions + numberTexturesPositions

    fun getTextureCoordinates(pos: Vec2i): FloatArray {
        val calcPointCoords = {p: Int, dim: Int ->
            Vec2(
                1f * p / dim,
                1f * (p + 1) / dim
            )
        }
        val xCoords = calcPointCoords(pos.x, cols)
        val yCoords = calcPointCoords(pos.y, rows)

        val getCoord = { point: Vec2, flag: Float ->
            if (MyMath.isZero(flag)) point.x else point.y
        }

        val ll = textureToSquareTemplateCoordinates.count() / 2

        val res = FloatArray(ll * 2)

        for (i in 0 until ll) {
            res[i * 2] = getCoord(xCoords, textureToSquareTemplateCoordinates[i * 2])
            res[i * 2 + 1] = getCoord(yCoords, textureToSquareTemplateCoordinates[i * 2 + 1])
        }

        return res
    }


    val textureCoordinates = texturesPositions.map { (t, p) ->
        t to getTextureCoordinates(p)
    }.toMap()

}