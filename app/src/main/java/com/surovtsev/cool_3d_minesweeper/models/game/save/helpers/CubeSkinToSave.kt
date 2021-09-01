package com.surovtsev.cool_3d_minesweeper.models.game.save.helpers

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.NeighboursCalculator
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.cell.CellSkin

class CubeSkinToSave(
    private val bombs: String,
    private val markedClosedEmpty: String,
) {
    companion object {
        const val SpaceChar = ' '
        const val BombChar = 'b'
        const val MarkedChar = 'm'
        const val ClosedChar = 'c'
        const val EmptyChar = 'e'

        private class SkinSaverHelper(
            val isBomb: Boolean,
            val isMarked: Boolean,
            val isClosed: Boolean,
            val isEmpty: Boolean,
        ) {
            companion object {
                fun createObject(cellSkin: CellSkin): SkinSaverHelper {
                    return SkinSaverHelper(
                        cellSkin.isBomb,
                        cellSkin.isMarked(),
                        cellSkin.isClosed(),
                        cellSkin.isEmpty()
                    )
                }

                fun createObject(
                    bomb: Char,
                    markedClosedEmpty: Char
                ): SkinSaverHelper {
                    return SkinSaverHelper(
                        bomb == BombChar,
                        markedClosedEmpty == MarkedChar,
                        markedClosedEmpty == ClosedChar,
                        markedClosedEmpty == EmptyChar
                    )
                }
            }

            fun getBombChar() = if (isBomb) BombChar else SpaceChar

            fun getMarkedClosedEmptyChar(): Char {
                if (isMarked) return MarkedChar
                if (isClosed) return ClosedChar
                if (isEmpty) return EmptyChar
                return SpaceChar
            }

            fun isOpened() = !isMarked && !isClosed &&  !isEmpty
        }

        fun createObject(cubeSkin: CubeSkin): CubeSkinToSave {
            val cellCount = cubeSkin.cellCount

            val resArr = { CharArray(cellCount) }
            val xx =
                {tc: Char, fc: Char ->
                    { flag: Boolean ->
                        if (flag) tc else fc
                    }
            }

            val bombs = resArr()
            val markedClosedEmpty = resArr()

            val bXX = xx(BombChar, SpaceChar)

            val skins = cubeSkin.skins
            cubeSkin.iterateCubes { xyz ->
                val skin = xyz.getValue(skins)
                val id = xyz.id

                val skinSaverHelper = SkinSaverHelper.createObject(skin)
                bombs[id] = skinSaverHelper.getBombChar()
                markedClosedEmpty[id] = skinSaverHelper.getMarkedClosedEmptyChar()
            }

            return CubeSkinToSave(
                bombs.joinToString(""),
                markedClosedEmpty.joinToString("")
            )
        }
    }

    fun applySavedData(
        cubeSkin: CubeSkin,
        gameLogic: GameLogic) {

        val bombsList = mutableListOf<CellIndex>()

        var openedBombCount = 0
        val skins = cubeSkin.skins
        cubeSkin.iterateCubes { xyz ->
            val skin = xyz.getValue(skins)
            val id = xyz.id

            val skinSaverHelper = SkinSaverHelper.createObject(
                bombs[id], markedClosedEmpty[id]
            )

            if (skinSaverHelper.isBomb) {
                skin.isBomb = true
                bombsList.add(xyz)
            }

            if (skinSaverHelper.isEmpty) {
                if (skin.isBomb) {
                    openedBombCount++
                }
                skin.setTexture(TextureCoordinatesHelper.TextureType.EMPTY)
            }
        }

        NeighboursCalculator.fillNeighbours(cubeSkin, bombsList)

        val closedBombs = bombsList.count() - openedBombCount
        cubeSkin.iterateCubes { xyz ->
            val skin = xyz.getValue(skins)
            val id = xyz.id

            val skinSaverHelper = SkinSaverHelper.createObject(
                bombs[id], markedClosedEmpty[id]
            )

            if (skinSaverHelper.isMarked) {
                skin.setTexture(TextureCoordinatesHelper.TextureType.MARKED)
            } else if (skinSaverHelper.isOpened()) {
                if (skin.isBomb) {
                    skin.setTexture(TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                } else {
                    skin.setNumbers()
                }
            }
        }

        gameLogic.setBombsLeft(closedBombs)
    }
}