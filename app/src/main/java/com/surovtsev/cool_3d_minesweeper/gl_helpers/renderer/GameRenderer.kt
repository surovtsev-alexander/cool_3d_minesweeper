package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.activities.TouchHandler
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.gl_helpers.helpers.ShaderHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    val _touchHandler = TouchHandler()

    private var program: Int = 0;

    private val _trianglesCoordinates = floatArrayOf(
        -1f, -1f, 0f,
        1f, -1f, 0f,
        0f, 1f, 0f
    )

    private val _indexes = intArrayOf(
        0, 1, 2
    )

    private val vertexArray: VertexArray

    private val POSITION_COMPONENT_COUNT = 3

    private val BYTES_PER_FLOAT = 4

    private val A_POSITION = "a_Position"
    private val U_COLOR  = "u_Color"
    private val U_VP_MATRIX = "u_VP_Matrix"
    private val U_M_MATRIX = "u_M_Matrix"

    private var _a_position_location = 0
    private var _ebo = 0;
    private var _u_color_location = 0
    private var _u_vp_matrix_location = 0
    private var _u_m_matrix_location = 0

    companion object {
        private fun matrix_creator() = FloatArray(16) { 0f }
    }

    val projectionMatrix = matrix_creator()
    val viewMatrix = matrix_creator()
    val viewProjectionMatrix = matrix_creator()


    init {
        vertexArray = VertexArray(_trianglesCoordinates)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)

        program = ShaderHelper.linkProgram(
            context, R.raw.vertex_shader, R.raw.fragment_shader
        )

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program)
        }

        glUseProgram(program)


        _a_position_location = glGetAttribLocation(program, A_POSITION)

        vertexArray.setVertexAttribPointer(0, _a_position_location,
            POSITION_COMPONENT_COUNT, 0)

        _u_color_location = glGetUniformLocation(program, U_COLOR)
        glUniform4f(_u_color_location, 1f, 0f, 0f, 1f)

        _u_vp_matrix_location = glGetUniformLocation(program, U_VP_MATRIX)
        _u_m_matrix_location = glGetUniformLocation(program, U_M_MATRIX)

        val buffers = intArrayOf(0)
         glGenBuffers(buffers.count(), buffers, 0)
        _ebo = buffers[0]
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _ebo)
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
                0, 30f,
                width.toFloat() / height.toFloat(), 1f, 10f
            )
        }
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.translateM(viewMatrix, 0, 0f, 0f, -5f)
        Matrix.multiplyMM(viewProjectionMatrix, 0,
            projectionMatrix, 0,
            viewMatrix, 0)

        glUseProgram(program)

        glUniformMatrix4fv(_u_vp_matrix_location, 1,
            false, viewProjectionMatrix, 0)
        glUniformMatrix4fv(_u_m_matrix_location, 1,
            false, _touchHandler._matrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        if (_touchHandler._updated) {
            _touchHandler.updateMatrix()
            glUniformMatrix4fv(_u_m_matrix_location, 1,
            false, _touchHandler._matrix, 0)
        }

        glDrawArrays(
            GL_TRIANGLES, 0
            , _trianglesCoordinates.count() / POSITION_COMPONENT_COUNT)
    }

}