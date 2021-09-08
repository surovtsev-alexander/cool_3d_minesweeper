package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper

import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.utils.math.MyMath
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import glm_.vec3.Vec3i

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

    val numberTextures = TextureType.values().drop(commonTexturesCount)

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
        val xCoords = calcPointCoords(pos[0],
            cols
        )
        val yCoords = calcPointCoords(pos[1],
            rows
        )

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


    val textureCoordinates: Map<TextureType, FloatArray>


    private val textureTypeCount = TextureType.values().count()
    private val counts = Vec3i(textureTypeCount)

    private val possibleSkins: Array<CellIndex>
    private val possibleTextureCoordinates: Map<Int, FloatArray>

    init {
        textureCoordinates = texturesPositions.map { (t, p) ->
            t to getTextureCoordinates(
                p
            )
        }.toMap()


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

        possibleTextureCoordinates = possibleSkins.map { it ->
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