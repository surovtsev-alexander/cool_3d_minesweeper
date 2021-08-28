package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig
import glm_.vec2.Vec2

interface IHaveUpdatedStatus {
    abstract fun isUpdated(): Boolean
}

open class Updatable(): IHaveUpdatedStatus {
    var updated = false

    fun update() {
        updated = true
    }

    fun release() {
        updated = false
    }

    override fun isUpdated()= updated
}

class ClickHandler(val cameraInfo: CameraInfo): Updatable() {
    private val pointerData = Pointer()

    val pointer: IPointer
        get() = pointerData

    var touchType = TouchType.SHORT
        private set

    fun handleClick(point: Vec2, touchType_: TouchType) {
        val proj = cameraInfo.normalizedDisplayCoordinates(point)
        pointerData.near = cameraInfo.calcNearByProj(proj)
        pointerData.far = cameraInfo.calcFarByProj(proj)

        touchType = touchType_
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