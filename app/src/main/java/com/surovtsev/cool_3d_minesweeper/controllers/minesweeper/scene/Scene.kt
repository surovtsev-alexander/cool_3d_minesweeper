package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchHandler
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.Pointer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.IntersectionCalculator
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CameraInfoHelper
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.models.gles.game_views_holder.GameViewsHolder
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper
import glm_.vec2.Vec2i

class Scene (
    private val gameLogic: GameLogic,
    gameObjectsHolder: GameObjectsHolder,
    cameraInfo: CameraInfo,
    private val timeSpanHelper: TimeSpanHelper,
    private val gameControls: GameControls,
    private val gameViewsHolder: GameViewsHolder
) {
    private val cameraInfoHelper = CameraInfoHelper(cameraInfo)
    private val pointer = Pointer()

    val moveHandler = MoveHandler(cameraInfoHelper)
    val touchHandler = TouchHandler(cameraInfoHelper, pointer)

    private val intersectionCalculator =
        IntersectionCalculator(
            pointer,
            gameObjectsHolder.cubeSkin,
            gameObjectsHolder.cubeBorder
        )

    fun onSurfaceChanged(newDisplaySize: Vec2i) {
        cameraInfoHelper.onSurfaceChanged(newDisplaySize)
    }

    fun onDrawFrame() {
        val cameraMoved = cameraInfoHelper.getAndRelease()
        val clicked = touchHandler.getAndRelease()

        if (gameControls.markOnShortTapControl.getAndRelease()) {
            gameLogic.markingOnShotTap = gameControls.markOnShortTapControl.isOn()
        }

        val glPointerView = gameViewsHolder.glPointerView
        val cubeView = gameViewsHolder.cubeView

        if (cameraMoved) {
            cameraInfoHelper.cameraInfo.recalculateMVPMatrix()
        }

        do {
//            if (clicked) {
//                gameObjectsHolder.glPointerView.turnOn()
//            }

            if (!glPointerView.isOn()) {
                break
            }

            if (clicked) {
                glPointerView.setPoints(touchHandler.pointer)
            }

            glPointerView.mGLESProgram!!.useProgram()
            if (cameraMoved) {
                with(glPointerView.mGLESProgram!!) {
                    fillMVP(cameraInfoHelper.cameraInfo.MVP)
                }
            }
            glPointerView.bindData()
            glPointerView.draw()
        } while (false)

        cubeView.cubeGLESProgram!!.useProgram()

        cubeView.bindData()

        gameLogic.openCubes()

        if (gameControls.removeMarkedBombsControl.getAndRelease()) {
            gameLogic.storeSelectedBombs()
        }
        if (gameControls.removeZeroBordersControl.getAndRelease()) {
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
            with(cubeView.cubeGLESProgram!!) {
                fillMVP(cameraInfoHelper.cameraInfo.MVP)
            }
        }
        cubeView.draw()
    }
}
