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

    var near = Vec3()
        private set
    var far  = Vec3()
        private set

    fun normalizedDisplayCoordinates(point: Vec2) = run {
        val pp = { p: Float, s: Float -> 2f * p / s - 1.0f }
        val x = pp(point.x, cameraInfo.mDisplayWidthF)
        val y = pp(point.y, cameraInfo.mDisplayHeightF) * -1f
        Vec2(x, y)
    }

    fun calc_near_by_proj(proj: Vec2) = calc_point_by_proj(-1f)(proj)
    fun calc_far_by_proj(proj: Vec2) = calc_point_by_proj(1f)(proj)

    fun calc_point_by_proj(z: Float): (Vec2) -> Vec3 {
        return fun (proj: Vec2): Vec3 {
            val vpx = Vec3(proj, z)
            val vx = mult_mat4_vec3(cameraInfo.mInvertedProjectionMatrix, vpx)
            return mult_mat4_vec3(cameraInfo.mInvertedViewMatrix, vx)
        }
    }

    fun handleClick(point: Vec2) {
        val proj = normalizedDisplayCoordinates(point)
        near = calc_near_by_proj(proj)
        far = calc_far_by_proj(proj)

        update()

        if (LoggerConfig.LOG_CLICK_HANDLER_DATA) {
            val message = arrayOf<String>(
                "proj:$proj",
                "near:$near",
                "far:$far"
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