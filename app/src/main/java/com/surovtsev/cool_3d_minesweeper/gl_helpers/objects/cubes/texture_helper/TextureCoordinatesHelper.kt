package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper

import com.surovtsev.cool_3d_minesweeper.math.Math
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

    enum class TextureTypes {
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

    val commonTexturesPositions = mapOf(
        TextureTypes.CLOSED to Vec2i(0, 0),
        TextureTypes.MARKED to Vec2i(1, 0),
        TextureTypes.EXPLODED_BOMB to Vec2i(2, 0),
    )

    val numberTexturesPositions = TextureTypes.values().let {
        val commonTexturesCount = 3
        (0 until it.count() - commonTexturesCount).map { x ->
            val col = x % cols
            val row = x / cols + 1
            it[x + commonTexturesCount] to Vec2i(col, row)
        }
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
            if (Math.isZero(flag)) point.x else point.y
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