package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper.mult_mat4_vec3
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.IClickReceiver
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4

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

class ClickHandler(val cameraInfo: CameraInfo): Updatable(), IClickReceiver {
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

    fun handleClick_old(screenX: Float, screenY: Float) {
        fun normalizedDisplayCoordinates() = run {
            val pp = { p: Float, s: Float -> 2f * p / s - 1.0f}
            val x = pp(screenX, cameraInfo.mDisplayWidthF)
            val y = pp(screenY, cameraInfo.mDisplayHeightF) * -1f

            Vec2(x, y)
        }

        val normalizedCoordinates = normalizedDisplayCoordinates()
        val clipCoordinates = Vec4(normalizedCoordinates, -1f, 1f)

        fun toEyeCoordinates(): Vec4 {
            val eyeCoordinates = cameraInfo.mInvertedProjectionMatrix * clipCoordinates
            return Vec4(Vec2(eyeCoordinates), -1f, 0f)
        }

        val eyeCoordinates = toEyeCoordinates()

        fun toWorldRay(): Vec4 {
            val rayWorld = cameraInfo.mInvertedViewMatrix * eyeCoordinates
            return rayWorld
        }

        val worldRay = toWorldRay()
        val mouseRay = Vec3(worldRay).normalize()

        if (LoggerConfig.LOG_CLICK_HANDLER_DATA) {
            val messages =  arrayOf<String>(
                "args: $screenX $screenY",
                "normalizedCoordinates: $normalizedCoordinates",
                "clipCoordinates: $clipCoordinates",
                "eyeCoordinates: $eyeCoordinates",
                "worldRay: $worldRay",
                "mouseRay: $mouseRay")
            ApplicationController.instance!!.messagesComponent!!.addMessageUI(messages.reduce { acc, x -> "$acc\n$x" })
        }
    }
}