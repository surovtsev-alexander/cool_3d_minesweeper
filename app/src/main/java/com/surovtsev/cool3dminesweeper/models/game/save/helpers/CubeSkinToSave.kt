package com.surovtsev.cool3dminesweeper.models.game.save.helpers

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.GameLogic
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.NeighboursCalculator
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.scene.texturecoordinateshelper.TextureCoordinatesHelper
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.CellIndex
import com.surovtsev.cool3dminesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool3dminesweeper.models.game.skin.cube.cell.CellSkin

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

            val bombs = resArr()
            val markedClosedEmpty = resArr()


            val skins = cubeSkin.skins
            cubeSkin.iterateCubes { cellIndex ->
                val skin = cellIndex.getValue(skins)
                val id = cellIndex.id

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
        cubeSkin.iterateCubes { cellIndex ->
            val skin = cellIndex.getValue(skins)
            val id = cellIndex.id

            val skinSaverHelper = SkinSaverHelper.createObject(
                bombs[id], markedClosedEmpty[id]
            )

            if (skinSaverHelper.isBomb) {
                skin.isBomb = true
                bombsList.add(cellIndex)
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
        cubeSkin.iterateCubes { cellIndex ->
            val skin = cellIndex.getValue(skins)
            val id = cellIndex.id

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