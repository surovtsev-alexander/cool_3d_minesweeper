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


package com.surovtsev.gamelogic.minesweeper.gamelogic

import com.surovtsev.core.helpers.gamelogic.TextureCoordinatesHelper
import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.Range3D
import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.BombPlacer
import com.surovtsev.gamelogic.models.game.interaction.GameControls
import com.surovtsev.gamelogic.utils.utils.gles.TextureUpdater
import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatus
import com.surovtsev.utils.androidview.interaction.TouchType

class GameTouchHandler(
    private val gameState: GameState,
    private val gameControls: GameControls,
    private val textureUpdater: TextureUpdater,
) {
    private data class PrevClickInfo(var id: Int, var time: Long)
    private val prevClickInfo =
        PrevClickInfo(
            -1,
            0L
        )
    private val doubleClickDelay = 200L

    private val gameStatusHolder = gameState.gameStatusHolder
    private val cubeSkin = gameState.cubeInfo.cubeSkin
    private val gameConfig = gameState.gameConfig
    private val neighboursCalculator = gameState.cubeInfo.neighboursCalculator

    fun touchCell(touchType: TouchType, pointedCell: PointedCell, currTime: Long) {
        val position = pointedCell.index

        if (gameStatusHolder.isGameOver()) {
            return
        }

        if (gameStatusHolder.isGameNotStarted()) {
            val bombsList = BombPlacer.placeBombs(
                gameConfig,
                cubeSkin,
                pointedCell.index,
                gameConfig.bombsCount
            )
            gameStatusHolder.setBombsLeft(bombsList.size)

            neighboursCalculator.fillNeighbours(bombsList)
            gameStatusHolder.setGameStatus(GameStatus.BombsPlaced)
        }

        val id = position.id
        when (touchType) {
            TouchType.SHORT -> {
                val flagging = gameControls.flagging
                if ((!flagging || pointedCell.skin.isOpenedNumber()) &&
                    id == prevClickInfo.id &&
                    currTime - prevClickInfo.time < doubleClickDelay) {
                    emptyCube(pointedCell)
                } else {
                    tryToOpenCube(pointedCell, flagging)
                }
            }
            TouchType.LONG -> {
                toggleFlaggingCell(pointedCell)
            }
        }

        prevClickInfo.id = id
        prevClickInfo.time = currTime
    }

    fun openCubes() {
        processOnElement(gameState.cubesToOpen, this::openCube)
    }

    fun removeCubes() {
        processOnElement(gameState.cubesToRemove, this::emptyCube)
    }

    fun storeSelectedBombs() {
        cubeSkin.skinsWithIndexes.forEach { (skin, cellIndex) ->
            if (skin.isFlagged()) {
                gameState.cubesToRemove.add(cellIndex)
            }
        }
    }

    fun collectOpenedNotEmptyBorders() {
        val cellsRange = gameConfig.cellsRange

        val sliceClearer = { r: Range3D ->
            when (inspectSlice((r))) {
                SliceDescription.HAS_CLOSED_CELLS -> {
                    true
                }
                SliceDescription.OPENED_AND_NOT_EMPTY -> {
                    emptySlice(r)
                    false
                }
                SliceDescription.EMPTY -> {
                    false
                }
            }
        }

        val sliceBothDirectionsIterator = { r: IntRange, c: (Int) -> Range3D ->

            val sliceIterator = { p: IntProgression ->
                for (v in p) {
                    val rr = c(v)
                    // stop on first NOT_EMPTY slice
                    val stop = sliceClearer(rr)
                    if (stop) {
                        break
                    }
                }
            }

            sliceIterator(r)
            sliceIterator(r.reversed())
        }

        sliceBothDirectionsIterator(cellsRange.xRange) { v -> cellsRange.copy(xRange = v..v) }
        sliceBothDirectionsIterator(cellsRange.yRange) { v -> cellsRange.copy(yRange = v..v) }
        sliceBothDirectionsIterator(cellsRange.zRange) { v -> cellsRange.copy(zRange = v..v) }
    }

    private fun emptyCube(pointedCell: PointedCell) {
        val skin = pointedCell.skin
        val isBomb = pointedCell.skin.isBomb

        if (skin.isEmpty()) {
            return
        }

        if (isBomb) {
            if (!skin.isFlagged()) {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                gameStatusHolder.setGameStatus(GameStatus.Lose)
                return
            }

            val action: (PointedCell, Int) -> Unit = {
                    c: PointedCell, i: Int ->
                do {
                    c.skin.neighbourBombs[i]--
                    if (!c.skin.isBomb) {
                        updateIfOpened(c)
                    }
                } while (false)
            }

            neighboursCalculator.iterateNeighbours(pointedCell.index, action)

            openNeighbours(pointedCell)


            gameStatusHolder.decBombsLeft()
        } else {
            if (skin.isFlagged()) {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                gameStatusHolder.setGameStatus(GameStatus.Lose)
                return
            }
        }

        setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EMPTY)

        gameStatusHolder.testIfWin()
    }

    private val removeCount = 1

    private fun processOnElement(list: MutableList<CellIndex>, action: (PointedCell) -> Unit) {
        for (i in 0 until removeCount) {
            if (gameStatusHolder.isGameOver()) {
                return
            }
            if (list.count() == 0) {
                return
            }

            action(cubeSkin.getPointedCell(list.removeAt(0)))
        }
    }

    private enum class SliceDescription {
        EMPTY,
        OPENED_AND_NOT_EMPTY,
        HAS_CLOSED_CELLS,
    }

    private fun emptySlice(cellsRange: Range3D) {
        cellsRange.iterate {
            val c = cubeSkin.getPointedCell(it)
            val s = c.skin
            if (!s.isEmpty()) {
                gameState.cubesToRemove.add(it)
            }
        }
    }

    private fun inspectSlice(cellsRange: Range3D): SliceDescription {
        var hasClosedCells = false
        var hasOpenedCells = false

        for (x in cellsRange.xRange) {
            for (y in cellsRange.yRange) {
                for (z in cellsRange.zRange) {
                    val p =
                        CellIndex(
                            x,
                            y,
                            z,
                            cellsRange.counts
                        )
                    val c = cubeSkin.getPointedCell(p)
                    val s = c.skin

                    if (s.isEmpty()) {
                        continue
                    } else if (s.isClosed()) {
                        hasClosedCells = true
                    } else {
                        hasOpenedCells = true
                    }

                    if (hasClosedCells) {
                        break
                    }
                }
                if (hasClosedCells) {
                    break
                }
            }
            if (hasClosedCells) {
                break
            }
        }

        return when {
            hasClosedCells -> SliceDescription.HAS_CLOSED_CELLS
            hasOpenedCells -> SliceDescription.OPENED_AND_NOT_EMPTY
            else -> SliceDescription.EMPTY
        }
    }

    private fun tryToOpenCube(pointedCell: PointedCell, flagIfClosed: Boolean) {
        val skin = pointedCell.skin
        if (skin.isClosed()) {
            if (flagIfClosed) {
                toggleFlaggingCell(pointedCell)
            } else {
                if (skin.isBomb) {
                    setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                    gameStatusHolder.setGameStatus(GameStatus.Lose)
                } else {
                    openCube(pointedCell)
                }
            }
        } else if (skin.isFlagged()) {
            if (flagIfClosed) {
                toggleFlaggingCell(pointedCell)
            }
        } else {
            openNeighboursIfBombsFlagged(pointedCell)
            openNeighbours(pointedCell)
        }
    }

    private fun openCube(pointedCell: PointedCell) {
        val skin = pointedCell.skin

        skin.setNumbers()
        skin.emptyIfZero()
        textureUpdater.updateTexture(pointedCell)

        openNeighbours(pointedCell)
    }

    private fun openNeighboursIfBombsFlagged(pointedCell: PointedCell) {
        if (pointedCell.skin.isBomb) {
            return
        }
        for (i in 0 until 3) {
            if (!neighboursCalculator.hasPosEmptyNeighbours(pointedCell.index, i)) {
                continue
            }

            val cubeNbhBombs = pointedCell.skin.neighbourBombs[i]

            val neighbours = neighboursCalculator.getNeighbours(pointedCell.index, i)

            val flagged = neighbours.count { it.skin.isFlagged() }

            if (cubeNbhBombs == flagged) {
                for (n in neighbours) {
                    if (n.skin.isClosed()) {
                        tryToOpenCube(n, false)
                    }
                }
            }
        }
    }

    private fun openNeighbours(pointedCell: PointedCell) {
        val neighbourBombs = pointedCell.skin.neighbourBombs
        val position = pointedCell.index

        for (i in 0 until 3) {
            val cubeNbhBombs = neighbourBombs[i]

            if (cubeNbhBombs > 0) {
                continue
            }

            if (!neighboursCalculator.hasPosEmptyNeighbours(position, i)) {
                continue
            }

            val neighbours = neighboursCalculator.getNeighbours(position, i)

            for (n in neighbours) {
                val p = n.index
                val s = n.skin

                if (!s.isClosed()) {
                    continue
                }

                if (s.isBomb) {
                    setCubeTexture(n, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                    gameStatusHolder.setGameStatus(GameStatus.Lose)
                    return
                }

                if (gameState.cubesToOpen.any { it == p }) {
                    continue
                }
                gameState.cubesToOpen.add(p)
            }
        }
    }

    private fun updateIfOpened(pointedCell: PointedCell) {
        val skin = pointedCell.skin

        if (skin.isEmpty() || skin.isClosed()) {
            return
        }

        skin.setNumbers()
        skin.emptyIfZero()
        textureUpdater.updateTexture(pointedCell)
    }

    private fun toggleFlaggingCell(pointedCell: PointedCell) {
        val skin = pointedCell.skin.texture[0]

        if (skin == TextureCoordinatesHelper.TextureType.CLOSED) {
            setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.FLAGGED)

            for (i in 0 until 3) {
                val neighbours = neighboursCalculator.getNeighbours(pointedCell.index, i)

                for (n in neighbours) {
                    if (n.skin.isOpenedNumber()) {
                        openNeighboursIfBombsFlagged(n)
                        openNeighbours(n)
                    }
                }
            }
        } else if (skin == TextureCoordinatesHelper.TextureType.FLAGGED) {
            setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.CLOSED)
        }
    }

    private fun setCubeTexture(
        pointedCell: PointedCell,
        textureType: TextureCoordinatesHelper.TextureType) {
        pointedCell.skin.setTexture(textureType)
        textureUpdater.updateTexture(pointedCell)
    }
}