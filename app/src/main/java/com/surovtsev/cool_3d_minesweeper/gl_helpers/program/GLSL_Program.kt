package com.surovtsev.cool_3d_minesweeper.gl_helpers.program

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.helpers.ShaderHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig

class GLSL_Program(val context: Context) {
    private var _programId = 0

    private val A_POSITION = "a_Position"
    private val U_COLOR  = "u_Color"
    private val U_VP_MATRIX = "u_VP_Matrix"
    private val U_M_MATRIX = "u_M_Matrix"

    var _a_position_location = 0
        private set
    private var _ebo = 0;
    private var _u_color_location = 0
    private var _u_vp_matrix_location = 0
    private var _u_m_matrix_location = 0

    fun load_program() {
        _programId = ShaderHelper.linkProgram(
            context, R.raw.vertex_shader, R.raw.fragment_shader
        )

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(_programId)
        }
    }

    fun load_uniforms() {
        _a_position_location = glGetAttribLocation(_programId, A_POSITION)

        _u_color_location = glGetUniformLocation(_programId, U_COLOR)
        glUniform4f(_u_color_location, 1f, 0f, 0f, 1f)

        _u_vp_matrix_location = glGetUniformLocation(_programId, U_VP_MATRIX)
        _u_m_matrix_location = glGetUniformLocation(_programId, U_M_MATRIX)
    }

    fun gen_buffers() {
        val buffers = intArrayOf(0)
        glGenBuffers(buffers.count(), buffers, 0)
        _ebo = buffers[0]
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _ebo)
    }

    fun set_vp_matrix(vp_matrix: FloatArray) {
        glUniformMatrix4fv(_u_vp_matrix_location, 1,
            false, vp_matrix, 0)
    }

    fun set_u_matrix(u_matrix: FloatArray) {
        glUniformMatrix4fv(_u_m_matrix_location, 1,
            false, u_matrix, 0)
    }

    fun use_program() {
        glUseProgram(_programId)
    }
}
