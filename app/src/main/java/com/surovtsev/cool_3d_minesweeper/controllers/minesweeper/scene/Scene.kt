package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomRealtime
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchHandler
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.Pointer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.logic.helpers.IntersectionCalculator

class Scene(
    private val gameObjectsHolder: GameObjectsHolder,
    private val rendererClock: CustomRealtime,
    width: Int,
    height: Int
) {
    private val cameraInfo = CameraInfo(width, height)
    private val pointer = Pointer()

    val moveHandler = MoveHandler(cameraInfo)
    val touchHandler = TouchHandler(cameraInfo, pointer)
    val removeBombs = Updatable(false)
    val removeBorderZeros = Updatable(false)

    val cellCalculator =
        IntersectionCalculator(
            pointer,
            gameObjectsHolder.cubeSkin,
            gameObjectsHolder.cubeBorder
        )


    fun onSurfaceChanged() {
        val glObject = gameObjectsHolder.cubeView

        val mVPMatrix = cameraInfo.MVP
        with(glObject.cubeGLESProgram) {
            useProgram()
            fillMVP(mVPMatrix)
        }

        with(gameObjectsHolder.glPointerView.mGLESProgram) {
            useProgram()
            fillMVP(mVPMatrix)
        }
    }

    fun onDrawFrame() {
        val cameraMoved = cameraInfo.getAndRelease()
        val clicked = touchHandler.isUpdated()

        if (cameraMoved) {
            cameraInfo.recalculateMVPMatrix()
        }

        do {
            if (clicked) {
                touchHandler.release()
                //gameObjectsHolder.glPointerView.turnOn()
            }

            if (!gameObjectsHolder.glPointerView.isOn()) {
                break
            }

            if (clicked) {
                gameObjectsHolder.glPointerView.setPoints(touchHandler.pointer)
            }

            gameObjectsHolder.glPointerView.mGLESProgram.useProgram()
            if (cameraMoved) {
                with(gameObjectsHolder.glPointerView.mGLESProgram) {
                    fillMVP(cameraInfo.MVP)
                }
            }
            gameObjectsHolder.glPointerView.bindData()
            gameObjectsHolder.glPointerView.draw()
        } while (false)

        val glCube = gameObjectsHolder.cubeView
        glCube.cubeGLESProgram.useProgram()

        glCube.bindData()

        val gameTouchHandler = gameObjectsHolder.gameTouchHandler
        gameTouchHandler.openCubes()

        if (removeBombs.getAndRelease()) {
            gameTouchHandler.storeSelectedBombs()
        }
        if (removeBorderZeros.getAndRelease()) {
            gameTouchHandler.storeZeroBorders()
        }
        gameTouchHandler.removeCubes()

        if (clicked) {
            val cell = cellCalculator.getIntersection()
            if (cell != null) {
                gameTouchHandler.touch(pointer.touchType, cell, rendererClock.time)
            }

        }
        if (cameraMoved) {
            with(glCube.cubeGLESProgram) {
                fillMVP(cameraInfo.MVP)
            }
        }
        glCube.draw()
    }
}
