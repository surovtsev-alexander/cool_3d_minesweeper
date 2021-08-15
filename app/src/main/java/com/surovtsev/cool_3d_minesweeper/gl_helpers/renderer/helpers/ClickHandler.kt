package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.utils.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IClickReceiver
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

class ClickHandler(val cameraInfo: CameraInfo): Updatable(),
    IClickReceiver {
    val mMessagesComponent = ApplicationController.instance!!.messagesComponent!!

    private val pointerData = Pointer()

    val pointer: IPointer
        get() = pointerData

    override fun handleClick(point: Vec2) {
        val proj = cameraInfo.normalizedDisplayCoordinates(point)
        pointerData.near = cameraInfo.calc_near_by_proj(proj)
        pointerData.far = cameraInfo.calc_far_by_proj(proj)

        update()

        if (LoggerConfig.LOG_CLICK_HANDLER_DATA) {
            val message = arrayOf<String>(
                "proj:$proj",
                "near:${pointerData.near}",
                "far:${pointerData.far}"
            ).reduce {acc, x -> "$acc\n$x"}
            ApplicationController.instance!!.messagesComponent!!.addMessageUI(message)
        }

        if (LoggerConfig.LOG_SCENE) {
            ApplicationController.instance!!.logScene?.invoke()
        }
    }
}