package com.surovtsev.gamescreen.views.glesrenderer

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.surovtsev.gamescreen.utils.utils.gles.interfaces.OpenGLEventsHandler
import com.surovtsev.gamescreen.dagger.GameScope
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@GameScope
class GLESRenderer @Inject constructor(
    private val openGLEventsHandler: OpenGLEventsHandler
): GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        openGLEventsHandler.onSurfaceCreated()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        openGLEventsHandler.onSurfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable (GL_BLEND)
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        openGLEventsHandler.onDrawFrame()
    }
}
