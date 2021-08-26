package com.surovtsev.cool_3d_minesweeper.game_logic

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.game_logic.data.PointedCube
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.ICanUpdateTexture
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHelper

class GameTouchHandler(val gameObject: GameObject, val textureUpdater: ICanUpdateTexture) {

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

    private var bombsList: BombsList = mutableListOf<GameObject.Position>()

    fun touch(clickType: ClickHelper.ClickType, pointedCube: PointedCube, currTime: Long) {
        val position = pointedCube.position

        if (state == GameState.NO_BOBMS_PLACED) {
            bombsList = BombPlacer.placeBombs(gameObject, pointedCube.position)

            NeighboursCalculator.fillNeighbours(gameObject, bombsList)
            state = GameState.BOMBS_PLACED
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


    private fun emptyCube(pointedCube: PointedCube) {
        val isBomb = pointedCube.description.isBomb

        setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EMPTY)

        if (isBomb) {
            val action = {
                    c: PointedCube, i: Int ->
                c.description.neighbourBombs[i]--

                updateIfOpened(c)
            }

            NeighboursCalculator.iterateNeightbours(
                gameObject, pointedCube.position, action)

            openNeighbours(pointedCube)
        }
    }

    val cubesToOpen = mutableListOf<GameObject.Position>()

    fun openCubes() {
        if (cubesToOpen.isEmpty()) {
            return
        }

        val x = cubesToOpen.removeAt(0)

        openNeighbours(gameObject.getPointedCube(x))
    }

    private fun tryToOpenCube(pointedCube: PointedCube) {
        val description = pointedCube.description
        if (description.isClosed()) {
            if (description.isBomb) {
                setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
            } else {
//                Log.d("TEST+++", "open cube $pointedCube")
                openCube(pointedCube)
            }
        } else {
//            Log.d("TEST+++", "open cube neighbours $pointedCube")
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
                if (d.isZero()) {
//                    Log.d("TEST+++", "ZERO")
                } else if (!d.isClosed()) {
//                    Log.d("TEST+++", "opened")
                    break
                }

//                Log.d("TEST+++", "added")
                val description = c.description
                description.setNumbers()
                description.emptyIfZero()
                textureUpdater.updateTexture(c)
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
