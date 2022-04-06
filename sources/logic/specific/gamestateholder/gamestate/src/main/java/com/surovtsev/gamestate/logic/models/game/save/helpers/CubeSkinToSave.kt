package com.surovtsev.gamestate.logic.models.game.save.helpers

import com.surovtsev.core.helpers.gamelogic.NeighboursCalculator
import com.surovtsev.core.helpers.gamelogic.TextureCoordinatesHelper
import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import com.surovtsev.core.models.game.skin.cube.cell.CellSkin
import com.surovtsev.gamestate.logic.models.game.cubeinfo.CubeInfo
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusHolder

class CubeSkinToSave(
    private val bombs: String,
    private val flaggedClosedEmpty: String,
) {
    companion object {
        const val SpaceChar = ' '
        const val BombChar = 'b'
        const val FlaggedChar = 'f'
        const val ClosedChar = 'c'
        const val EmptyChar = 'e'

        private class SkinSaverHelper(
            val isBomb: Boolean,
            val isFlagged: Boolean,
            val isClosed: Boolean,
            val isEmpty: Boolean,
        ) {
            companion object {
                fun createObject(cellSkin: CellSkin): SkinSaverHelper {
                    return SkinSaverHelper(
                        cellSkin.isBomb,
                        cellSkin.isFlagged(),
                        cellSkin.isClosed(),
                        cellSkin.isEmpty()
                    )
                }

                fun createObject(
                    bomb: Char,
                    flaggedClosedEmpty: Char
                ): SkinSaverHelper {
                    return SkinSaverHelper(
                        bomb == BombChar,
                        flaggedClosedEmpty == FlaggedChar,
                        flaggedClosedEmpty == ClosedChar,
                        flaggedClosedEmpty == EmptyChar
                    )
                }
            }

            fun getBombChar() = if (isBomb) BombChar else SpaceChar

            fun getFlaggedClosedEmptyChar(): Char {
                if (isFlagged) return FlaggedChar
                if (isClosed) return ClosedChar
                if (isEmpty) return EmptyChar
                return SpaceChar
            }

            fun isOpened() = !isFlagged && !isClosed &&  !isEmpty
        }

        fun createObject(
            gameConfig: GameConfig,
            cubeSkin: CubeSkin
        ): CubeSkinToSave {
            val cellsCount = gameConfig.cellsCount

            val resArr = { CharArray(cellsCount) }

            val bombs = resArr()
            val flaggedClosedEmpty = resArr()


            val skins = cubeSkin.skins
            gameConfig.cellsRange.iterate { cellIndex ->
                val skin = cellIndex.getValue(skins)
                val id = cellIndex.id

                val skinSaverHelper = SkinSaverHelper.createObject(skin)
                bombs[id] = skinSaverHelper.getBombChar()
                flaggedClosedEmpty[id] = skinSaverHelper.getFlaggedClosedEmptyChar()
            }

            return CubeSkinToSave(
                bombs.joinToString(""),
                flaggedClosedEmpty.joinToString("")
            )
        }
    }

    fun applySavedData(
        gameConfig: GameConfig,
        cubeInfo: CubeInfo,
        gameStatusHolder: GameStatusHolder,
    ) {
        val cellsRange = gameConfig.cellsRange

        val bombsList = mutableListOf<CellIndex>()

        var openedBombCount = 0
        val skins = cubeInfo.cubeSkin.skins
        cellsRange.iterate { cellIndex ->
            val skin = cellIndex.getValue(skins)
            val id = cellIndex.id

            val skinSaverHelper = SkinSaverHelper.createObject(
                bombs[id], flaggedClosedEmpty[id]
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

        cubeInfo.neighboursCalculator.fillNeighbours(bombsList)

        val closedBombs = bombsList.count() - openedBombCount
        cellsRange.iterate { cellIndex ->
            val skin = cellIndex.getValue(skins)
            val id = cellIndex.id

            val skinSaverHelper = SkinSaverHelper.createObject(
                bombs[id], flaggedClosedEmpty[id]
            )

            if (skinSaverHelper.isFlagged) {
                skin.setTexture(TextureCoordinatesHelper.TextureType.FLAGGED)
            } else if (skinSaverHelper.isOpened()) {
                if (skin.isBomb) {
                    skin.setTexture(TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                } else {
                    skin.setNumbers()
                }
            }
        }

        gameStatusHolder.setBombsLeft(closedBombs)
    }
}