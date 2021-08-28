package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.Pointer
import glm_.vec2.Vec2


class TouchHandler(
    val cameraInfo: CameraInfo,
    val pointer: Pointer
):
    Updatable(false)
{
    fun handleTouch(point: Vec2, touchType: TouchType) {
        val proj = cameraInfo.normalizedDisplayCoordinates(point)
        pointer.near = cameraInfo.calcNearByProj(proj)
        pointer.far = cameraInfo.calcFarByProj(proj)
        pointer.touchType = touchType

        update()

        if (LoggerConfig.LOG_CLICK_HANDLER_DATA) {
            val message = arrayOf<String>(
                "proj:$proj",
                "near:${pointer.near}",
                "far:${pointer.far}"
            ).reduce {acc, x -> "$acc\n$x"}
            ApplicationController.instance!!.messagesComponent?.addMessageUI(message)
        }
    }
}