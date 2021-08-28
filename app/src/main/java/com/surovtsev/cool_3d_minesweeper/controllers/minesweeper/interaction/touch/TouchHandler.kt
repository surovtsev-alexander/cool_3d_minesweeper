package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.IPointer
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.Pointer
import glm_.vec2.Vec2

interface IHaveUpdatedStatus {
    abstract fun isUpdated(): Boolean
}

open class Updatable():
    IHaveUpdatedStatus {
    var updated = false

    fun update() {
        updated = true
    }

    fun release() {
        updated = false
    }

    override fun isUpdated()= updated
}

class TouchHandler(
    val cameraInfo: CameraInfo,
    val pointer: Pointer
):
    Updatable()
{
    var touchType = TouchType.SHORT
        private set

    fun handleClick(point: Vec2, touchType_: TouchType) {
        val proj = cameraInfo.normalizedDisplayCoordinates(point)
        pointer.near = cameraInfo.calcNearByProj(proj)
        pointer.far = cameraInfo.calcFarByProj(proj)

        touchType = touchType_
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