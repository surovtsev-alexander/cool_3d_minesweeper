package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import com.surovtsev.cool_3d_minesweeper.game_logic.GameObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.texture_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHelper
import glm_.vec3.Vec3i

class GameTouchHandler(val gameObject: GameObject, val textureUpdater: ICanUpdateTexture) {

    private data class PrevClickInfo(var id: Int, var time: Long)
    private val prevClickInfo = PrevClickInfo(-1, 0L)
    private val doubleClickDelay = 200L

    fun touch(clickType: ClickHelper.ClickType, pointedCube: GLCubes.PointedCube, currTime: Long) {
        val id = pointedCube.id
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

    private fun emptyCube(pointedCube: GLCubes.PointedCube) {
        setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EMPTY)
    }

    private fun tryToOpenCube(pointedCube: GLCubes.PointedCube) {
        val description = pointedCube.description
        when (description.texture[0]) {
            TextureCoordinatesHelper.TextureType.CLOSED -> {
                if (description.isBomb) {
                    setCubeTexture(pointedCube, TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                } else {
                    description.neighbourBombs = Vec3i(0, 2, 7)

                    for (i in 0 until 3) {
                        description.texture[i] = TextureCoordinatesHelper.numberTextures[description.neighbourBombs[i]]
                    }
                }
            }
        }
    }

    private fun toggleMarkingCube(pointedCube: GLCubes.PointedCube) {
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
        pointedCube: GLCubes.PointedCube,
        textureType: TextureCoordinatesHelper.TextureType) {
        pointedCube.description.setTexture(textureType)
        textureUpdater.updateTexture(pointedCube)
    }
}
