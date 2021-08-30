package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchHandler
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.Pointer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.IntersectionCalculator
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfoHelper
import com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper
import com.surovtsev.cool_3d_minesweeper.views.opengl.CubeView
import glm_.vec2.Vec2i

class Scene (
    private val gameLogic: GameLogic,
    gameObjectsHolder: GameObjectsHolder,
    cameraInfo: CameraInfo,
    private val timeSpanHelper: TimeSpanHelper,
    displaySize: Vec2i,
    var cubeView: CubeView?,
    var glPointerView: GLPointerView?
) {
    val cameraInfoHelper = CameraInfoHelper(cameraInfo, displaySize)
    private val pointer = Pointer()

    val moveHandler = MoveHandler(cameraInfoHelper)
    val touchHandler = TouchHandler(cameraInfoHelper, pointer)
    val removeBombs = Updatable(false)
    val removeBorderZeros = Updatable(false)

    private val intersectionCalculator =
        IntersectionCalculator(
            pointer,
            gameObjectsHolder.cubeSkin,
            gameObjectsHolder.cubeBorder
        )

    fun onSurfaceChanged() {
        val mVPMatrix = cameraInfoHelper.cameraInfo.MVP
        with(cubeView!!.cubeGLESProgram) {
            useProgram()
            fillMVP(mVPMatrix)
        }

        with(glPointerView!!.mGLESProgram) {
            useProgram()
            fillMVP(mVPMatrix)
        }
    }

    fun onDrawFrame() {
        val cameraMoved = cameraInfoHelper.getAndRelease()
        val clicked = touchHandler.isUpdated()

        if (cameraMoved) {
            cameraInfoHelper.cameraInfo.recalculateMVPMatrix()
        }

        do {
            if (clicked) {
                touchHandler.release()
                //gameObjectsHolder.glPointerView.turnOn()
            }

            if (!glPointerView!!.isOn()) {
                break
            }

            if (clicked) {
                glPointerView!!.setPoints(touchHandler.pointer)
            }

            glPointerView!!.mGLESProgram.useProgram()
            if (cameraMoved) {
                with(glPointerView!!.mGLESProgram) {
                    fillMVP(cameraInfoHelper.cameraInfo.MVP)
                }
            }
            glPointerView!!.bindData()
            glPointerView!!.draw()
        } while (false)

        val glCube = cubeView!!
        glCube.cubeGLESProgram.useProgram()

        glCube.bindData()

        gameLogic.openCubes()

        if (removeBombs.getAndRelease()) {
            gameLogic.storeSelectedBombs()
        }
        if (removeBorderZeros.getAndRelease()) {
            gameLogic.storeZeroBorders()
        }
        gameLogic.removeCubes()

        if (clicked) {
            val cell = intersectionCalculator.getCell()
            if (cell != null) {
                gameLogic.touchCell(pointer.touchType, cell, timeSpanHelper.timeAfterDeviceStartup)
            }

        }
        if (cameraMoved) {
            with(glCube.cubeGLESProgram) {
                fillMVP(cameraInfoHelper.cameraInfo.MVP)
            }
        }
        glCube.draw()
    }
}
