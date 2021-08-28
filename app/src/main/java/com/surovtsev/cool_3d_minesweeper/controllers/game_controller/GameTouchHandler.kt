package com.surovtsev.cool_3d_minesweeper.controllers.game_controller

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.models.game.*
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers.BombPlacer
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers.NeighboursCalculator
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.ICanUpdateTexture
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.ClickHelper
import java.lang.StringBuilder

class GameTouchHandler(
    val gameObject: GameObject,
    val textureUpdater: ICanUpdateTexture,
    val gameStatusesReceiver: IGameStatusesReceiver
) {


    var state = GameStatus.NO_BOBMS_PLACED


    private data class PrevClickInfo(var id: Int, var time: Long)
    private val prevClickInfo =
        PrevClickInfo(
            -1,
            0L
        )
    private val doubleClickDelay = 200L

    private val bombsList = mutableListOf<CubePosition>()
    val cubesToOpen = mutableListOf<CubePosition>()
    private val cubesToRemove = mutableListOf<CubePosition>()

    private var bombsLeft = 0

    fun touch(clickType: ClickHelper.ClickType, pointedCube: PointedCube, currTime: Long) {
        val position = pointedCube.position

        if (gameIsOver()) {
            return
        }

        if (state == GameStatus.NO_BOBMS_PLACED) {
            bombsList += BombPlacer.placeBombs(gameObject, pointedCube.position)
            bombsLeft = bombsList.size

            NeighboursCalculator.fillNeighbours(gameObject, bombsList)
            setGameState(GameStatus.BOMBS_PLACED)
        }

        val id = position.id
        when (clickType) {
            ClickHelper.ClickType.CLICK -> {
                if (id == prevClickInfo.id && currTime - prevClickInfo.time < doubleClickDelay) {
                    emptyCube(pointedCube)
                } else {
                    tryToOpenCube(pointedCube)
                }
            }
            ClickHelper.ClickType.LONG_CLICK -> {
                toggleMarkingCube(pointedCube)
            }
        }

        prevClickInfo.id = id
        prevClickInfo.time = currTime
    }

    private fun setGameState(newState: GameStatus) {
        state = newState

        gameStatusesReceiver.gameStatusUpdated(state)
    }

    private fun emptyCube(pointedCube: PointedCube) {
        val description = pointedCube.description
        val isBomb = pointedCube.description.isBomb

        if (isBomb) {
            if (!description.isMarked()) {
                setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                setGameState(GameStatus.LOSE)
                return
            }

            val action: (PointedCube, Int) -> Unit = {
                    c: PointedCube, i: Int ->
                do {
                    c.description.neighbourBombs[i]--
                    if (!c.description.isBomb) {
                        updateIfOpened(c)
                    }
                } while (false)
            }

            NeighboursCalculator.iterateNeightbours(
                gameObject, pointedCube.position, action)

            openNeighbours(pointedCube)

            bombsLeft--;
        } else {
            if (description.isMarked()) {
                setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                setGameState(GameStatus.LOSE)
                return
            }
        }

        setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EMPTY)

        if (bombsLeft == 0) {
            setGameState(GameStatus.WIN)
        }
    }

    val removeCount = 1

    private fun gameIsOver() = GameStatusHelper.isGameOver(state)

    private fun processOnElement(list: MutableList<CubePosition>, action: (PointedCube) -> Unit) {
        for (i in 0 until removeCount) {
            if (gameIsOver()) {
                return
            }
            if (list.count() == 0) {
                return
            }

            action(gameObject.getPointedCube(list.removeAt(0)))
        }
    }

    fun openCubes() {
        processOnElement(cubesToOpen, this::openCube)
    }

    fun removeCubes() {
        processOnElement(cubesToRemove, this::emptyCube)
    }

    fun storeSelectedBombs() {
        gameObject.iterateCubes { xyz ->
            do {
                val p = gameObject.getPointedCube(xyz)
                val d = p.description
                if (d.isMarked()) {
                    cubesToRemove.add(xyz)
                }
            } while (false)
        }
    }

    fun storeZeroBorders() {
        val ranges = gameObject.ranges

        var stop = false

        val l = { r: DimRanges ->
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

//        for (x in ranges.xRange) {
//            val r = ranges.copy(xRange = x..x)
//            val stop = l(r)
////            Log.d("TEST++", "r $r\tstop $stop")
//            if (stop) {
//                break
//            }
//        }
//
//        for (x in ranges.xRange.reversed()) {
//            val r = ranges.copy(xRange = x..x)
//            val stop = l(r)
////            Log.d("TEST++", "r $r\tstop $stop")
//            if (stop) {
//                break
//            }
//        }


        val xx = { r: IntRange, c: (Int) -> DimRanges ->

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

        xx(ranges.xRange, { v -> ranges.copy(xRange = v..v) })
        xx(ranges.yRange, { v -> ranges.copy(yRange = v..v) })
        xx(ranges.zRange, { v -> ranges.copy(zRange = v..v) })
    }

    private enum class SliceDescription {
        EMPTY,
        NOT_EMPTY,
        HAS_ZERO
    }

    private fun emptySlice(ranges: DimRanges) {
//        Log.d("TEST++", "emptySlice $ranges")
        ranges.iterate(gameObject.counts) {
            val c = gameObject.getPointedCube(it)
            val d = c.description
            if (!d.isEmpty()) {
//                Log.d("TEST++", "$it")
                cubesToRemove.add(it)
            }
        }
    }

    private fun inspectSlice(ranges: DimRanges): SliceDescription {
        var notEmpty = false
        var hasZero = false

        val counts = gameObject.counts
        for (x in ranges.xRange) {
            for (y in ranges.yRange) {
                for (z in ranges.zRange) {
                    val p = CubePosition(x, y, z, counts)
                    val c = gameObject.getPointedCube(p)
                    val d = c.description

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

    private fun tryToOpenCube(pointedCube: PointedCube) {
        val description = pointedCube.description
        if (description.isClosed()) {
            if (description.isBomb) {
                setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                setGameState(GameStatus.LOSE)
            } else {
//                Log.d("TEST+++", "open cube $pointedCube")
                openCube(pointedCube)
            }
        } else {
//            Log.d("TEST+++", "open cube neighbours $pointedCube")
            openNeighboursIfBombsMarked(pointedCube)
            openNeighbours(pointedCube)
        }
    }

    private fun openCube(pointedCube: PointedCube) {
        val description = pointedCube.description

//        Log.d("TEST++", "openCube ${description.neighbourBombs}")

        description.setNumbers()
        description.emptyIfZero()
        textureUpdater.updateTexture(pointedCube)

        openNeighbours(pointedCube)
    }

    private fun openNeighboursIfBombsMarked(pointedCube: PointedCube) {
        for (i in 0 until 3) {
            val cubeNbhBombs = pointedCube.description.neighbourBombs[i]

            if (!NeighboursCalculator.hasPosEmptyNeighbours(
                    gameObject, pointedCube.position, i, null
                )) {
                continue
            }

            val neighbours = NeighboursCalculator.getNeighbours(
                gameObject, pointedCube.position, i)

            val marked = neighbours.count { it.description.isMarked() }

            if (cubeNbhBombs == marked) {
                for (n in neighbours) {
                    if (n.description.isClosed()) {
                        tryToOpenCube(n)
                    }
                }
            }
        }
    }

    private fun openNeighbours(pointedCube: PointedCube) {
        val sb = StringBuilder()

        val neighbourBombs = pointedCube.description.neighbourBombs
        val position = pointedCube.position

        sb.append("---\nopenNeighbours $position $neighbourBombs\n")
        for (i in 0 until 3) {
            val cubeNbhBombs = neighbourBombs[i]

            sb.append("i $i\n")

            if (cubeNbhBombs > 0) {
                continue
            }

            // need to be modified. check surrendings
            if (!NeighboursCalculator.hasPosEmptyNeighbours(
                    gameObject, position, i, sb
                )) {
                sb.append("it has not empty neighbours\n")
                continue
            }

            val neighbours = NeighboursCalculator.getNeighbours(
                gameObject, position, i)

            for (n in neighbours) {
                val p = n.position
                val d = n.description

                if (!d.isClosed()) {
                    continue
                }

                if (d.isBomb) {
                    setCubeTexture(n, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                    setGameState(GameStatus.LOSE)
                    return
                }

                if (cubesToOpen.any { it == p }) {
                    Log.d("TEST+++", "already added\n")
                    continue
                }
                sb.append("n ${p} ${d.neighbourBombs}\n")
                cubesToOpen.add(p)
            }
        }
        sb.append("--cubesToOpen.count ${cubesToOpen.count()}\n")
        sb.append(
            cubesToOpen.map { it.id.toString() }.fold("") { acc , s -> "$acc $s"} + "\n"
        )
        Log.d("TEST+++", sb.toString())
    }

    private fun openNeighbours_old(pointedCube: PointedCube) {
        val action = {
                c: PointedCube ->
            do {
//                Log.d("TEST+++", "open $c")
                if (cubesToOpen.any { it == c.position }) {
//                    Log.d("TEST+++", "already added")
                    break
                }

                val d = c.description
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
                val description = c.description
                cubesToOpen.add(c.position)
            } while (false)
        }

        val description = pointedCube.description
//        if ( description.isZero() ||
//            (description.isBomb && description.isEmpty())) {
//            NeighboursCalculator.iterateAllNeighbours(
//                gameObject, pointedCube.position, action
//            )
//        }
    }

    private fun updateIfOpened(pointedCube: PointedCube) {
        val description = pointedCube.description

        if (description.isEmpty() || description.isClosed()) {
            return
        }

        description.setNumbers()
        description.emptyIfZero()
        textureUpdater.updateTexture(pointedCube)
    }

    private fun toggleMarkingCube(pointedCube: PointedCube) {
        when (pointedCube.description.texture[0]) {
            TextureCoordinatesHelper.TextureType.CLOSED -> {
                setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.MARKED)
            }
            TextureCoordinatesHelper.TextureType.MARKED -> {
                setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.CLOSED)
            }
        }
    }

    private fun setCubeTexture(
        pointedCube: PointedCube,
        textureType: TextureCoordinatesHelper.TextureType) {
        pointedCube.description.setTexture(textureType)
        textureUpdater.updateTexture(pointedCube)
    }
}
