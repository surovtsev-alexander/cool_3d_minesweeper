package com.surovtsev.utils.gles.renderer

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.surovtsev.utils.math.camerainfo.CameraInfoHelper
import glm_.vec2.Vec2i
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLESRenderer(
    private val cameraInfoHelper: CameraInfoHelper
): GLSurfaceView.Renderer {

    var openGLEventsHandler: OpenGLEventsHandler? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        openGLEventsHandler?.onSurfaceCreated()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        val displaySize = Vec2i(width, height)
        cameraInfoHelper.onSurfaceChanged(displaySize)

        openGLEventsHandler?.onSurfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable (GL_BLEND)
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        openGLEventsHandler?.onDrawFrame()
    }
}
