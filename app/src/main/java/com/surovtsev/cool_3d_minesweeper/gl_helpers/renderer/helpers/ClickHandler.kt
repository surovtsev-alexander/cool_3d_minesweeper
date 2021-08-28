package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
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

    var clickType = ClickHelper.ClickType.CLICK
        private set

    fun handleClick(point: Vec2, clickType_: ClickHelper.ClickType) {
        val proj = cameraInfo.normalizedDisplayCoordinates(point)
        pointerData.near = cameraInfo.calcNearByProj(proj)
        pointerData.far = cameraInfo.calcFarByProj(proj)

        clickType = clickType_
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