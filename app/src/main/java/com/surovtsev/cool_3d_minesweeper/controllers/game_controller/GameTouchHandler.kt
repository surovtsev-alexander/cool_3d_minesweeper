package com.surovtsev.cool_3d_minesweeper.controllers.game_controller

import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers.BombPlacer
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers.NeighboursCalculator
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.models.game.CubeSkin
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellRange
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.ICanUpdateTexture

class GameTouchHandler(
    val cubeSkin: CubeSkin,
    val textureUpdater: ICanUpdateTexture,
    val gameStatusesReceiver: IGameStatusesReceiver,
    private val gameConfig: GameConfig
) {


    var state = GameStatus.NO_BOBMS_PLACED


    private data class PrevClickInfo(var id: Int, var time: Long)
    private val prevClickInfo =
        PrevClickInfo(
            -1,
            0L
        )
    private val doubleClickDelay = 200L

    private val bombsList = mutableListOf<CellIndex>()
    private val cubesToOpen = mutableListOf<CellIndex>()
    private val cubesToRemove = mutableListOf<CellIndex>()

    var bombsLeft = 0
        private set

    fun touch(touchType: TouchType, pointedCell: PointedCell, currTime: Long) {
        val position = pointedCell.index

        if (gameIsOver()) {
            return
        }

        if (state == GameStatus.NO_BOBMS_PLACED) {
            bombsList += BombPlacer.placeBombs(cubeSkin, pointedCell.index, gameConfig.bombsCount)
            bombsLeft = bombsList.size
            gameStatusesReceiver.bombCountUpdated()

            NeighboursCalculator.fillNeighbours(cubeSkin, bombsList)
            setGameState(GameStatus.BOMBS_PLACED)
        }

        val id = position.id
        when (touchType) {
            TouchType.SHORT -> {
                if (id == prevClickInfo.id && currTime - prevClickInfo.time < doubleClickDelay) {
                    emptyCube(pointedCell)
                } else {
                    tryToOpenCube(pointedCell)
                }
            }
            TouchType.LONG -> {
                toggleMarkingCube(pointedCell)
            }
        }

        prevClickInfo.id = id
        prevClickInfo.time = currTime
    }

    private fun setGameState(newState: GameStatus) {
        state = newState

        gameStatusesReceiver.gameStatusUpdated(state)
    }

    private fun emptyCube(pointedCell: PointedCell) {
        val description = pointedCell.skin
        val isBomb = pointedCell.skin.isBomb

        if (isBomb) {
            if (!description.isMarked()) {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                setGameState(GameStatus.LOSE)
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

            NeighboursCalculator.iterateNeightbours(
                cubeSkin, pointedCell.index, action)

            openNeighbours(pointedCell)

            bombsLeft--;
            gameStatusesReceiver.bombCountUpdated()
        } else {
            if (description.isMarked()) {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                setGameState(GameStatus.LOSE)
                return
            }
        }

        setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EMPTY)

        if (bombsLeft == 0) {
            setGameState(GameStatus.WIN)
        }
    }

    val removeCount = 1

    private fun gameIsOver() = GameStatusHelper.isGameOver(state)

    private fun processOnElement(list: MutableList<CellIndex>, action: (PointedCell) -> Unit) {
        for (i in 0 until removeCount) {
            if (gameIsOver()) {
                return
            }
            if (list.count() == 0) {
                return
            }

            action(cubeSkin.getPointedCube(list.removeAt(0)))
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
                val p = cubeSkin.getPointedCube(xyz)
                val d = p.skin
                if (d.isMarked()) {
                    cubesToRemove.add(xyz)
                }
            } while (false)
        }
    }

    fun storeZeroBorders() {
        val cellRange = cubeSkin.cellRange

        var stop = false

        val l = { r: CellRange ->
            val sd = inspectSlice(r)

//            Log.d("TEST++", "sd $sd")
            when (sd) {
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
//                Log.d("TEST++", "r $r\tstop $stop")
                    if (stop) {
                        break
                    }
                }
            }

            fv(r)
            fv(r.reversed())
        }

        xx(cellRange.xRange, { v -> cellRange.copy(xRange = v..v) })
        xx(cellRange.yRange, { v -> cellRange.copy(yRange = v..v) })
        xx(cellRange.zRange, { v -> cellRange.copy(zRange = v..v) })
    }

    private enum class SliceDescription {
        EMPTY,
        NOT_EMPTY,
        HAS_ZERO
    }

    private fun emptySlice(cellRange: CellRange) {
        cellRange.iterate(cubeSkin.counts) {
            val c = cubeSkin.getPointedCube(it)
            val d = c.skin
            if (!d.isEmpty()) {
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
                    val c = cubeSkin.getPointedCube(p)
                    val d = c.skin

                    if (d.isEmpty()) {
                        continue
                    } else if (d.isClosed() || d.isMarked()) {
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

        if (notEmpty) {
            return SliceDescription.NOT_EMPTY
        } else if (hasZero) {
            return SliceDescription.HAS_ZERO
        } else {
            return SliceDescription.EMPTY
        }
    }

    private fun tryToOpenCube(pointedCell: PointedCell) {
        val description = pointedCell.skin
        if (description.isClosed()) {
            if (description.isBomb) {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                setGameState(GameStatus.LOSE)
            } else {
//                Log.d("TEST+++", "open cube $pointedCube")
                openCube(pointedCell)
            }
        } else {
//            Log.d("TEST+++", "open cube neighbours $pointedCube")
            openNeighboursIfBombsMarked(pointedCell)
            openNeighbours(pointedCell)
        }
    }

    private fun openCube(pointedCell: PointedCell) {
        val description = pointedCell.skin

//        Log.d("TEST++", "openCube ${description.neighbourBombs}")

        description.setNumbers()
        description.emptyIfZero()
        textureUpdater.updateTexture(pointedCell)

        openNeighbours(pointedCell)
    }

    private fun openNeighboursIfBombsMarked(pointedCell: PointedCell) {
        for (i in 0 until 3) {
            val cubeNbhBombs = pointedCell.skin.neighbourBombs[i]

            if (!NeighboursCalculator.hasPosEmptyNeighbours(
                    cubeSkin, pointedCell.index, i, null
                )) {
                continue
            }

            val neighbours = NeighboursCalculator.getNeighbours(
                cubeSkin, pointedCell.index, i)

            val marked = neighbours.count { it.skin.isMarked() }

            if (cubeNbhBombs == marked) {
                for (n in neighbours) {
                    if (n.skin.isClosed()) {
                        tryToOpenCube(n)
                    }
                }
            }
        }
    }

    private fun openNeighbours(pointedCell: PointedCell) {
//        val sb = StringBuilder()

        val neighbourBombs = pointedCell.skin.neighbourBombs
        val position = pointedCell.index

//        sb.append("---\nopenNeighbours $position $neighbourBombs\n")
        for (i in 0 until 3) {
            val cubeNbhBombs = neighbourBombs[i]

//            sb.append("i $i\n")

            if (cubeNbhBombs > 0) {
                continue
            }

            // need to be modified. check surrendings
            if (!NeighboursCalculator.hasPosEmptyNeighbours(
                    cubeSkin, position, i, null
                )) {
//                sb.append("it has not empty neighbours\n")
                continue
            }

            val neighbours = NeighboursCalculator.getNeighbours(
                cubeSkin, position, i)

            for (n in neighbours) {
                val p = n.index
                val d = n.skin

                if (!d.isClosed()) {
                    continue
                }

                if (d.isBomb) {
                    setCubeTexture(n, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                    setGameState(GameStatus.LOSE)
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

    private fun openNeighbours_old(pointedCell: PointedCell) {
        val action = {
                c: PointedCell ->
            do {
//                Log.d("TEST+++", "open $c")
                if (cubesToOpen.any { it == c.index }) {
//                    Log.d("TEST+++", "already added")
                    break
                }

                val d = c.skin
                if (d.isMarked()) {
                    break
                }

                if (d.isZero()) {
//                    Log.d("TEST+++", "ZERO")
                } else if (!d.isClosed()) {
//                    Log.d("TEST+++", "opened")
                    break
                }

//                Log.d("TEST+++", "added")
                val description = c.skin
                cubesToOpen.add(c.index)
            } while (false)
        }

        val description = pointedCell.skin
//        if ( description.isZero() ||
//            (description.isBomb && description.isEmpty())) {
//            NeighboursCalculator.iterateAllNeighbours(
//                gameObject, pointedCube.position, action
//            )
//        }
    }

    private fun updateIfOpened(pointedCell: PointedCell) {
        val description = pointedCell.skin

        if (description.isEmpty() || description.isClosed()) {
            return
        }

        description.setNumbers()
        description.emptyIfZero()
        textureUpdater.updateTexture(pointedCell)
    }

    private fun toggleMarkingCube(pointedCell: PointedCell) {
        when (pointedCell.skin.texture[0]) {
            TextureCoordinatesHelper.TextureType.CLOSED -> {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.MARKED)
            }
            TextureCoordinatesHelper.TextureType.MARKED -> {
                setCubeTexture(pointedCell, TextureCoordinatesHelper.TextureType.CLOSED)
            }
        }
    }

    private fun setCubeTexture(
        pointedCell: PointedCell,
        textureType: TextureCoordinatesHelper.TextureType) {
        pointedCell.skin.setTexture(textureType)
        textureUpdater.updateTexture(pointedCell)
    }
}
