package com.surovtsev.cool_3d_minesweeper.models.game.camera_info

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.IPointer
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.Pointer
import glm_.vec2.Vec2

class TouchHandler(
    private val cameraInfo: CameraInfo
): Updatable(false) {
    private val pointerData =
        Pointer()

    val pointer: IPointer
        get() = pointerData

    var clickType =
        TouchType.SHORT
        private set

    fun handleTouch(point: Vec2, touchType_: TouchType) {
        val proj = cameraInfo.normalizedDisplayCoordinates(point)
        pointerData.near = cameraInfo.calcNearByProj(proj)
        pointerData.far = cameraInfo.calcFarByProj(proj)

        clickType = touchType_

        update()

        if (LoggerConfig.LOG_CLICK_HANDLER_DATA) {
            val message = arrayOf<String>(
                "proj:$proj",
                "near:${pointerData.near}",
                "far:${pointerData.far}"
            ).reduce {acc, x -> "$acc\n$x"}
            ApplicationController.instance!!.messagesComponent?.addMessageUI(message)
        }
    }
}