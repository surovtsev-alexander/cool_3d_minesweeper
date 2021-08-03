package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper.mult_mat4_vec3
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
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

class ClickHandler(val cameraInfo: CameraInfo): Updatable() {
    val mMessagesComponent = ApplicationController.instance!!.messagesComponent!!

    var x_near = Vec3()
        private set
    var x_far  = Vec3()
        private set

    fun handleClick(screenX: Float, screenY: Float) {
        fun normalizedDisplayCoordinates() = run {
            val pp = { p: Float, s: Float -> 2f * p / s - 1.0f }
            val x = pp(screenX, cameraInfo.mDisplayWidthF)
            val y = pp(screenY, cameraInfo.mDisplayHeightF) * -1f
            Vec2(x, y)
        }

        class VPX(val vpx: Vec3) {
            val vx = mult_mat4_vec3(cameraInfo.mInvertedProjectionMatrix, vpx)
            val x = mult_mat4_vec3(cameraInfo.mInvertedViewMatrix, vx)

            override fun toString(): String =
                "vpx: $vpx\nvx: $vx\nx: $x"
        }

        val vpx = normalizedDisplayCoordinates()
        val near = VPX(Vec3(vpx, -1f))
        val far = VPX(Vec3(vpx, 1f))


        x_near = near.x
        x_far = far.x
        update()

        if (LoggerConfig.LOG_CLICK_HANDLER_DATA) {
            val message = arrayOf<String>(
                "vpx:$vpx",
                "x_near:$near",
                "x_far:$far"
            ).reduce {acc, x -> "$acc\n$x"}
            ApplicationController.instance!!.messagesComponent!!.addMessageUI(message)
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

        if (LoggerConfig.LOG_SCENE) {
            ApplicationController.instance!!.logScene?.invoke()
        }
    }
}