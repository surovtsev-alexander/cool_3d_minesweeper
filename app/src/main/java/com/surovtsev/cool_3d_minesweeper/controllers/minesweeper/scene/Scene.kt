package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomRealtime
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchHandler
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.DrawablePointer

class Scene(
    private val gameObjectsHolder: GameObjectsHolder,
    private val rendererClock: CustomRealtime,
    width: Int,
    height: Int
) {
    private val cameraInfo = CameraInfo(width, height)
    private val pointer = DrawablePointer()

    val moveHandler = MoveHandler(cameraInfo)
    val touchHandler = TouchHandler(cameraInfo, pointer)
    val removeBombs = Updatable(false)
    val removeBorderZeros = Updatable(false)

    fun onSurfaceChanged() {
        val glObject = gameObjectsHolder.glCubes.glObject

        val mVPMatrix = cameraInfo.MVP
        with(glObject.modelModelGLSLProgram) {
            use_program()
            fillU_MVP(mVPMatrix)
        }

        with(gameObjectsHolder.glPointerView.mGLSLProgram) {
            use_program()
            fillU_MVP(mVPMatrix)
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
                gameObjectsHolder.glPointerView.needToBeDrawn = true
            }

            if (!gameObjectsHolder.glPointerView.needToBeDrawn) {
                break
            }

            if (clicked) {
                gameObjectsHolder.glPointerView.setPoints(touchHandler.pointer)
            }

            if (pointer.isOn()) {
                gameObjectsHolder.glPointerView.mGLSLProgram.use_program()
                if (cameraMoved) {
                    with(gameObjectsHolder.glPointerView.mGLSLProgram) {
                        fillU_MVP(cameraInfo.MVP)
                    }
                }
                gameObjectsHolder.glPointerView.bindData()
                gameObjectsHolder.glPointerView.draw()
            }
        } while (false)


        val glCubes = gameObjectsHolder.glCubes
        val glObject = glCubes.glObject
        glObject.modelModelGLSLProgram.use_program()

        glObject.bindData()

        val gameTouchHandler = glCubes.gameTouchHandler
        gameTouchHandler.openCubes()

        if (removeBombs.getAndRelease()) {
            gameTouchHandler.storeSelectedBombs()
        }
        if (removeBorderZeros.getAndRelease()) {
            //gameTouchHandler.openCubes()
            gameTouchHandler.storeZeroBorders()
        }
        gameTouchHandler.removeCubes()

        if (clicked) {
            glCubes.testPointer(touchHandler.pointer, rendererClock.time)
        }
        if (cameraMoved) {
            with(glObject.modelModelGLSLProgram) {
                fillU_MVP(cameraInfo.MVP)
            }
        }
        glObject.draw()
    }
}
