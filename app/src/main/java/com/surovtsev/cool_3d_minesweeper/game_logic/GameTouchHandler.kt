package com.surovtsev.cool_3d_minesweeper.game_logic

import com.surovtsev.cool_3d_minesweeper.game_logic.data.PointedCube
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.GLCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.ICanUpdateTexture
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHelper
import glm_.vec3.Vec3i

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

    private var bombsList: BombsList = mutableListOf<Vec3i>()

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
            val cubesToOpen =
                mutableListOf<PointedCube>()

            val action = {
                    c: PointedCube, i: Int ->
                c.description.neighbourBombs[i]--

                updateIfOpened(c)
            }

            NeighboursCalculator.iterateNeightbours(
                gameObject, pointedCube.position.getVec(), action)
        }
    }

    private fun tryToOpenCube(pointedCube: PointedCube) {
        val description = pointedCube.description
        when (description.texture[0]) {
            TextureCoordinatesHelper.TextureType.CLOSED -> {
                if (description.isBomb) {
                    setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                } else {
                    description.setNumbers()
                    textureUpdater.updateTexture(pointedCube)
                }
            }
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
