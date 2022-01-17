package com.surovtsev.gamelogic.minesweeper.gamelogic

import com.surovtsev.core.helpers.gamelogic.NeighboursCalculator
import com.surovtsev.core.helpers.gamelogic.TextureCoordinatesHelper
import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.CellRange
import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.BombPlacer
import com.surovtsev.gamelogic.models.game.interaction.GameControls
import com.surovtsev.gamelogic.utils.utils.gles.TextureUpdater
import com.surovtsev.gamestate.GameState
import com.surovtsev.gamestate.models.game.gamestatus.GameStatus
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

    fun touchCell(touchType: TouchType, pointedCell: PointedCell, currTime: Long) {
        val position = pointedCell.index

        if (gameStatusHolder.isGameOver()) {
            return
        }

        if (gameStatusHolder.isGameNotStarted()) {
            val bombsList = BombPlacer.placeBombs(
                cubeSkin,
                pointedCell.index,
                gameState.gameConfig.bombsCount
            )
            gameStatusHolder.setBombsLeft(bombsList.size)

            NeighboursCalculator.fillNeighbours(cubeSkin, bombsList)
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
        cubeSkin.iterateCubes { cellIndex ->
            do {
                val p = cubeSkin.getPointedCell(cellIndex)
                val s = p.skin
                if (s.isFlagged()) {
                    gameState.cubesToRemove.add(cellIndex)
                }
            } while (false)
        }
    }

    fun collectOpenedNotEmptyBorders() {
        val cellRange = cubeSkin.cellRange

        val sliceClearer = { r: CellRange ->
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

        val sliceBothDirectionsIterator = { r: IntRange, c: (Int) -> CellRange ->

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

        sliceBothDirectionsIterator(cellRange.xRange) { v -> cellRange.copy(xRange = v..v) }
        sliceBothDirectionsIterator(cellRange.yRange) { v -> cellRange.copy(yRange = v..v) }
        sliceBothDirectionsIterator(cellRange.zRange) { v -> cellRange.copy(zRange = v..v) }
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

            NeighboursCalculator.iterateNeighbours(
                cubeSkin, pointedCell.index, action)

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

    private fun emptySlice(cellRange: CellRange) {
        cellRange.iterate(cubeSkin.counts) {
            val c = cubeSkin.getPointedCell(it)
            val s = c.skin
            if (!s.isEmpty()) {
                gameState.cubesToRemove.add(it)
            }
        }
    }

    private fun inspectSlice(cellRange: CellRange): SliceDescription {
        var hasClosedCells = false
        var hasOpenedCells = false

        val counts = cubeSkin.counts
        for (x in cellRange.xRange) {
            for (y in cellRange.yRange) {
                for (z in cellRange.zRange) {
                    val p =
                        CellIndex(
                            x,
                            y,
                            z,
                            counts
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
            if (!NeighboursCalculator.hasPosEmptyNeighbours(
                    cubeSkin, pointedCell.index, i
                )) {
                continue
            }

            val cubeNbhBombs = pointedCell.skin.neighbourBombs[i]

            val neighbours = NeighboursCalculator.getNeighbours(
                cubeSkin, pointedCell.index, i)

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

            if (!NeighboursCalculator.hasPosEmptyNeighbours(
                    cubeSkin, position, i
                )) {
                continue
            }

            val neighbours = NeighboursCalculator.getNeighbours(
                cubeSkin, position, i)

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
                val neighbours = NeighboursCalculator.getNeighbours(
                    cubeSkin, pointedCell.index, i
                )

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