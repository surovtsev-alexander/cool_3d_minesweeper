package com.surovtsev.cool_3d_minesweeper.activities

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.util.ShaderHelper
import com.surovtsev.cool_3d_minesweeper.util.TextResourceReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    private var _program: Int = 0;

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        _program = ShaderHelper.linkProgram(
            ShaderHelper.compileVertextShader(
                TextResourceReader.readTextFileFromResource(
                    context, R.raw.vertex_shader
                )
            ),
            ShaderHelper.compileFragmentShader(
                TextResourceReader.readTextFileFromResource(
                    context, R.raw.fragment_shader
                )
            )
        )

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(_program)
        }

        glUseProgram(_program)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
    }

}