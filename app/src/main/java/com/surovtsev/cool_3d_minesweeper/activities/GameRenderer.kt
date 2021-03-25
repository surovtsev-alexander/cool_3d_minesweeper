package com.surovtsev.cool_3d_minesweeper.activities

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.util.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    private var _program: Int = 0;

    private var _vertexData: FloatBuffer? = null

    private val _trianglesCoordinates = floatArrayOf(
        -1f, -1f, 0f,
        1f, -1f, 0f,
        0f, 1f, 0f
    )
    private val POSITION_COMPONENT_COUNT = 3

    private val BYTES_PER_FLOAT = 4

    private val A_POSITION = "a_Position"
    private val U_COLOR  = "u_Color"
    private val U_MATRIX = "u_Matrix"

    private var _a_position_location = 0
    private var _u_color_location = 0
    private var _u_matrix_location = 0


    companion object {
        private fun matrix_creator() = FloatArray(16) { 0f }
    }

    val projectionMatrix = matrix_creator()
    val viewMatrix = matrix_creator()
    val viewProjectionMatrix = matrix_creator()


    init {
        _vertexData = ByteBuffer
            .allocateDirect(_trianglesCoordinates.count() * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        _vertexData!!.put(_trianglesCoordinates)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)

        _program = ShaderHelper.linkProgram(
            context, R.raw.vertex_shader, R.raw.fragment_shader
        )

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(_program)
        }

        glUseProgram(_program)


        _a_position_location = glGetAttribLocation(_program, A_POSITION)
        _vertexData!!.position(0)
        glVertexAttribPointer(_a_position_location, POSITION_COMPONENT_COUNT,
            GL_FLOAT, false, 0, _vertexData)

        glEnableVertexAttribArray(_a_position_location)

        _u_color_location = glGetUniformLocation(_program, U_COLOR)
        glUniform4f(_u_color_location, 1f, 0f, 0f, 1f)

        _u_matrix_location = glGetUniformLocation(_program, U_MATRIX)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        if (false) {
            MatrixHelper.perspectiveM(
                projectionMatrix,
                45f,
                width.toFloat() / height.toFloat(),
                1f, 10f
            )
        } else {
            Matrix.perspectiveM(
                projectionMatrix,
                0, 90f,
                width.toFloat() / height.toFloat(), 1f, 10f
            )
        }
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.translateM(viewMatrix, 0, 0f, 0f, -2f)
        Matrix.multiplyMM(viewProjectionMatrix, 0,
            projectionMatrix, 0,
            viewMatrix, 0)

        glUseProgram(_program)

        glUniformMatrix4fv(_u_matrix_location, 1,
            false, viewProjectionMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glDrawArrays(
            GL_TRIANGLES, 0
            , _trianglesCoordinates.count() / POSITION_COMPONENT_COUNT)
    }

}