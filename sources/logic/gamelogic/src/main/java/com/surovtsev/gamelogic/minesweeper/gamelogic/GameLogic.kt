package com.surovtsev.gamelogic.minesweeper.gamelogic

import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.BombPlacer
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.GameLogicStateHelper
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.NeighboursCalculator
import com.surovtsev.gamelogic.minesweeper.scene.texturecoordinateshelper.TextureCoordinatesHelper
import com.surovtsev.gamelogic.models.game.cellpointers.CellIndex
import com.surovtsev.gamelogic.models.game.cellpointers.CellRange
import com.surovtsev.gamelogic.models.game.cellpointers.PointedCell
import com.surovtsev.gamelogic.models.game.config.GameConfig
import com.surovtsev.gamelogic.models.game.gamestatus.GameStatus
import com.surovtsev.gamelogic.models.game.interaction.GameControls
import com.surovtsev.gamelogic.models.game.skin.cube.CubeSkin
import com.surovtsev.gamelogic.utils.utils.gles.TextureUpdater
import com.surovtsev.utils.androidview.interaction.TouchType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class GameLogic(
    private val cubeSkin: CubeSkin,
    private val textureUpdater: TextureUpdater,
    private val gameConfig: GameConfig,
    val gameLogicStateHelper: GameLogicStateHelper,
    val gameControls: GameControls,
) {

    private val _bombsLeftFlow = MutableStateFlow(0)
    val bombsLeftFlow: BombsLeftFlow = _bombsLeftFlow.asStateFlow()

    private data class PrevClickInfo(var id: Int, var time: Long)
    private val prevClickInfo =
        PrevClickInfo(
            -1,
            0L
        )
    private val doubleClickDelay = 200L

    val cubesToOpen = mutableListOf<CellIndex>()
    val cubesToRemove = mutableListOf<CellIndex>()

    fun applySavedData(
        cubesToOpen_: List<CellIndex>,
        cubesToRemove_: List<CellIndex>) {
        cubesToOpen += cubesToOpen_
        cubesToRemove += cubesToRemove_
    }

    @Suppress("SpellCheckingInspection")
    fun touchCell(touchType: TouchType, pointedCell: PointedCell, currTime: Long) {
        val position = pointedCell.index

        if (gameLogicStateHelper.isGameOver()) {
            return
        }

        if (gameLogicStateHelper.isGameNotStarted()) {
            val bombsList = BombPlacer.placeBombs(cubeSkin, pointedCell.index, gameConfig.bombsCount)
            setBombsLeft(bombsList.size)

            NeighboursCalculator.fillNeighbours(cubeSkin, bombsList)
            gameLogicStateHelper.setGameState(GameStatus.BombsPlaced)
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

    fun setBombsLeft(v: Int) {
        _bombsLeftFlow.value = v
    }

    private fun decBombsLeft() {
        _bombsLeftFlow.value -= 1
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
                gameLogicStateHelper.setGameState(GameStatus.Lose)
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


            decBombsLeft()
        } else {
            if (skin.isFlagged()) {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                gameLogicStateHelper.setGameState(GameStatus.Lose)
                return
            }
        }

        setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EMPTY)

        if (_bombsLeftFlow.value == 0) {
            gameLogicStateHelper.setGameState(GameStatus.Win)
        }
    }

    private val removeCount = 1

    private fun processOnElement(list: MutableList<CellIndex>, action: (PointedCell) -> Unit) {
        for (i in 0 until removeCount) {
            if (gameLogicStateHelper.isGameOver()) {
                return
            }
            if (list.count() == 0) {
                return
            }

            action(cubeSkin.getPointedCell(list.removeAt(0)))
        }
    }

    fun openCubes() {
        processOnElement(cubesToOpen, this::openCube)
    }

    fun removeCubes() {
        processOnElement(cubesToRemove, this::emptyCube)
    }

    fun storeSelectedBombs() {
        cubeSkin.iterateCubes { cellIndex ->
            do {
                val p = cubeSkin.getPointedCell(cellIndex)
                val s = p.skin
                if (s.isFlagged()) {
                    cubesToRemove.add(cellIndex)
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
                cubesToRemove.add(it)
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
                    gameLogicStateHelper.setGameState(GameStatus.Lose)
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
                    gameLogicStateHelper.setGameState(GameStatus.Lose)
                    return
                }

                if (cubesToOpen.any { it == p }) {
                    continue
                }
                cubesToOpen.add(p)
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
