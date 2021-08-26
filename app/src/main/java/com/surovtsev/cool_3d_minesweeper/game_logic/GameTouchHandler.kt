package com.surovtsev.cool_3d_minesweeper.game_logic

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.game_logic.data.CubePosition
import com.surovtsev.cool_3d_minesweeper.game_logic.data.PointedCube
import com.surovtsev.cool_3d_minesweeper.game_logic.interfaces.IHaveGameStatusProcessor
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.ICanUpdateTexture
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHelper

class GameTouchHandler(
    val gameObject: GameObject,
    val textureUpdater: ICanUpdateTexture,
    val gameStatusProcessor: IHaveGameStatusProcessor
) {

    enum class GameState {
        NO_BOBMS_PLACED,
        BOMBS_PLACED,
        WIN,
        LOSE
    }

    var state = GameState.NO_BOBMS_PLACED


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

        if (state == GameState.NO_BOBMS_PLACED) {
            bombsList += BombPlacer.placeBombs(gameObject, pointedCube.position)
            bombsLeft = bombsList.size

            NeighboursCalculator.fillNeighbours(gameObject, bombsList)
            setGameState(GameState.BOMBS_PLACED)
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

    private fun setGameState(newState: GameState) {
        state = newState

        if (gameIsOver()) {
            gameStatusProcessor.gameStatusUpdated(state)
        }
    }

    private fun emptyCube(pointedCube: PointedCube) {
        val description = pointedCube.description
        val isBomb = pointedCube.description.isBomb

        if (isBomb) {
            if (!description.isMarked()) {
                setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                setGameState(GameState.LOSE)
                return
            }

            val action: (PointedCube, Int) -> Unit = {
                    c: PointedCube, i: Int ->
                do {
                    if (c.description.isBomb) {
                        break
                    }

                    c.description.neighbourBombs[i]--

                    if (false && c.description.neighbourBombs[i] == 0) {
                        cubesToRemove.add(c.position)
                    } else {
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
                setGameState(GameState.LOSE)
                return
            }
        }

        setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EMPTY)

        if (bombsLeft == 0) {
            setGameState(GameState.WIN)
        }
    }

    val removeCount = 10

    fun gameIsOver() = (state == GameState.WIN || state == GameState.LOSE)

    fun processOnElement(list: MutableList<CubePosition>, action: (PointedCube) -> Unit) {
        for (i in 0 until 10) {
            if (gameIsOver()) {
                return
            }
            if (list.isEmpty()) {
                return
            }

            action(gameObject.getPointedCube(list.removeAt(0)))
        }
    }

    fun openCubes() {
        processOnElement(cubesToOpen, this::tryToOpenCube)
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

    }

    private fun tryToOpenCube(pointedCube: PointedCube) {
        val description = pointedCube.description
        if (description.isClosed()) {
            if (description.isBomb) {
                setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                setGameState(GameState.LOSE)
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
        description.setNumbers()
        description.emptyIfZero()
        textureUpdater.updateTexture(pointedCube)

        openNeighbours(pointedCube)
    }

    private fun openNeighboursIfBombsMarked(pointedCube: PointedCube) {
        for (i in 0 until 3) {
            val cubeNbhBombs = pointedCube.description.neighbourBombs[i]
            if (cubeNbhBombs == 0) {
                continue
            }
            val neighbours = NeighboursCalculator.getNeighbours(
                gameObject, pointedCube.position, i)

            val marked = neighbours.count { it.description.isMarked() }

            if (cubeNbhBombs == marked) {
                for (n in neighbours) {
                    if (n.description.isClosed()) {
                        cubesToOpen.add(n.position)
                    }
                }
            }
        }
    }

    private fun openNeighbours(pointedCube: PointedCube) {
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
        if (description.hasZero() ||
            (description.isBomb && description.isEmpty())) {
            NeighboursCalculator.iterateAllNeighbours(
                gameObject, pointedCube.position, action
            )
        }
    }

    private fun updateIfOpened(pointedCube: PointedCube) {
        val description = pointedCube.description

        if (description.isEmpty() || description.isClosed()) {
            return
        }

        description.setNumbers()
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
