package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.BombPlacer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.GameLogicStateHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.NeighboursCalculator
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_model_view.helpers.GameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellRange
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.ICanUpdateTexture

class GameLogic(
    private val cubeSkin: CubeSkin,
    private val textureUpdater: ICanUpdateTexture,
    private val gameConfig: GameConfig,
    private val gameEventsReceiver: GameEventsReceiver,
    val gameLogicStateHelper: GameLogicStateHelper
) {

    private data class PrevClickInfo(var id: Int, var time: Long)
    private val prevClickInfo =
        PrevClickInfo(
            -1,
            0L
        )
    private val doubleClickDelay = 200L

    var markingOnShotTap = false

    val cubesToOpen = mutableListOf<CellIndex>()
    val cubesToRemove = mutableListOf<CellIndex>()

    private var bombsLeft = 0

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
                if ((!markingOnShotTap || pointedCell.skin.isOpenedNumber()) &&
                    id == prevClickInfo.id &&
                    currTime - prevClickInfo.time < doubleClickDelay) {
                    emptyCube(pointedCell)
                } else {
                    tryToOpenCube(pointedCell, markingOnShotTap)
                }
            }
            TouchType.LONG -> {
                toggleMarkingCube(pointedCell)
            }
        }

        prevClickInfo.id = id
        prevClickInfo.time = currTime
    }

    fun setBombsLeft(v: Int) {
        bombsLeft = v
        notifyBombsCountUpdated()
    }

    private fun decBombsLeft() {
        bombsLeft--
        notifyBombsCountUpdated()
    }

    fun notifyBombsCountUpdated() {
        gameEventsReceiver.bombCountUpdated(bombsLeft)
    }

    private fun emptyCube(pointedCell: PointedCell) {
        val skin = pointedCell.skin
        val isBomb = pointedCell.skin.isBomb

        if (skin.isEmpty()) {
            return
        }

        if (isBomb) {
            if (!skin.isMarked()) {
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
            if (skin.isMarked()) {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                gameLogicStateHelper.setGameState(GameStatus.Lose)
                return
            }
        }

        setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EMPTY)

        if (bombsLeft == 0) {
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
        cubeSkin.iterateCubes { xyz ->
            do {
                val p = cubeSkin.getPointedCell(xyz)
                val s = p.skin
                if (s.isMarked()) {
                    cubesToRemove.add(xyz)
                }
            } while (false)
        }
    }

    fun storeZeroBorders() {
        val cellRange = cubeSkin.cellRange

        val l = { r: CellRange ->
            when (inspectSlice((r))) {
                SliceDescription.NOT_EMPTY -> {
                    true
                }
                SliceDescription.HAS_ZERO -> {
                    emptySlice(r)
                    false
                }
                SliceDescription.EMPTY -> {
                    false
                }
            }
        }

        val xx = { r: IntRange, c: (Int) -> CellRange ->

            val fv = { p: IntProgression ->
                for (v in p) {
                    val rr = c(v)
                    val stop = l(rr)
                    if (stop) {
                        break
                    }
                }
            }

            fv(r)
            fv(r.reversed())
        }

        xx(cellRange.xRange) { v -> cellRange.copy(xRange = v..v) }
        xx(cellRange.yRange) { v -> cellRange.copy(yRange = v..v) }
        xx(cellRange.zRange) { v -> cellRange.copy(zRange = v..v) }
    }

    private enum class SliceDescription {
        EMPTY,
        NOT_EMPTY,
        HAS_ZERO
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
        var notEmpty = false
        var hasZero = false

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
                        notEmpty = true
                    } else {
                        hasZero = true
                    }

                    if (notEmpty) {
                        break
                    }
                }
                if (notEmpty) {
                    break
                }
            }
            if (notEmpty) {
                break
            }
        }

        return when {
            notEmpty -> SliceDescription.NOT_EMPTY
            hasZero -> SliceDescription.HAS_ZERO
            else -> SliceDescription.EMPTY
        }
    }

    private fun tryToOpenCube(pointedCell: PointedCell, markIfClosed: Boolean) {
        val skin = pointedCell.skin
        if (skin.isClosed()) {
            if (markIfClosed) {
                toggleMarkingCube(pointedCell)
            } else {
                if (skin.isBomb) {
                    setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                    gameLogicStateHelper.setGameState(GameStatus.Lose)
                } else {
                    openCube(pointedCell)
                }
            }
        } else if (skin.isMarked()) {
            if (markIfClosed) {
                toggleMarkingCube(pointedCell)
            }
        } else {
            openNeighboursIfBombsMarked(pointedCell)
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

    private fun openNeighboursIfBombsMarked(pointedCell: PointedCell) {
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

            val marked = neighbours.count { it.skin.isMarked() }

            if (cubeNbhBombs == marked) {
                for (n in neighbours) {
                    if (n.skin.isClosed()) {
                        tryToOpenCube(n, false)
                    }
                }
            }
        }
    }

    private fun openNeighbours(pointedCell: PointedCell) {
//        val sb = StringBuilder()

        val neighbourBombs = pointedCell.skin.neighbourBombs
        val position = pointedCell.index

        @Suppress("SpellCheckingInspection")
//        sb.append("---\nopenNeighbours $position $neighbourBombs\n")
        for (i in 0 until 3) {
            val cubeNbhBombs = neighbourBombs[i]

//            sb.append("i $i\n")

            if (cubeNbhBombs > 0) {
                continue
            }

            // need to be modified. check surroundings
            if (!NeighboursCalculator.hasPosEmptyNeighbours(
                    cubeSkin, position, i
                )) {
//                sb.append("it has not empty neighbours\n")
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
//                    Log.d("TEST+++", "already added\n")
                    continue
                }
//                sb.append("n ${p} ${d.neighbourBombs}\n")
                cubesToOpen.add(p)
            }
        }
//        sb.append("--cubesToOpen.count ${cubesToOpen.count()}\n")
//        sb.append(
//            cubesToOpen.map { it.id.toString() }.fold("") { acc , s -> "$acc $s"} + "\n"
//        )
//        Log.d("TEST+++", sb.toString())
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

    private fun toggleMarkingCube(pointedCell: PointedCell) {
        val skin = pointedCell.skin.texture[0]

        if (skin == TextureCoordinatesHelper.TextureType.CLOSED) {
            setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.MARKED)

            for (i in 0 until 3) {
                val neighbours = NeighboursCalculator.getNeighbours(
                    cubeSkin, pointedCell.index, i
                )

                for (n in neighbours) {
                    if (n.skin.isOpenedNumber()) {
                        openNeighboursIfBombsMarked(n)
                        openNeighbours(n)
                    }
                }
            }
        } else if (skin == TextureCoordinatesHelper.TextureType.MARKED) {
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
