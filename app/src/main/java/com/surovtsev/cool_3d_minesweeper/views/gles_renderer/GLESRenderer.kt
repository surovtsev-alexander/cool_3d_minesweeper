package com.surovtsev.cool_3d_minesweeper.views.gles_renderer

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameScope
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.IHandleOpenGLEvents
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@GameScope
class GLESRenderer @Inject constructor(
    private val openglEventsHandler: IHandleOpenGLEvents
): GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        openglEventsHandler.onSurfaceCreated()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        openglEventsHandler.onSurfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable (GL_BLEND)
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        openglEventsHandler.onDrawFrame()
    }
}
