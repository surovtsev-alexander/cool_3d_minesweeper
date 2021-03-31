package com.surovtsev.cool_3d_minesweeper.gl_helpers.program

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.gl_helpers.helpers.ShaderHelper
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import glm_.mat4x4.Mat4
import java.nio.FloatBuffer

class GLSL_Program(val context: Context) {
    private var _programId = 0

    private val A_POSITION = "a_Position"
    private val A_TRIANGLE_NUM = "a_TriangleNum"
    private val A_TRIANGLE_TEXTURE = "a_TriangleTexture"
    private val U_COLOR  = "u_Color"
    private val U_VP_MATRIX = "u_VP_Matrix"
    private val U_M_MATRIX = "u_M_Matrix"

    var _a_position_location = 0
        private set
    var _a_triangle_num = 0
        private set
    var _a_triangle_texture = 0
        private set
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

    fun load_locations() {
        _a_position_location = glGetAttribLocation(_programId, A_POSITION)
        _a_triangle_num = glGetAttribLocation(_programId, A_TRIANGLE_NUM)
        _a_triangle_texture = glGetAttribLocation(_programId, A_TRIANGLE_TEXTURE)

        _u_color_location = glGetUniformLocation(_programId, U_COLOR)
        glUniform4f(_u_color_location, 1f, 0f, 0f, 1f)

        _u_vp_matrix_location = glGetUniformLocation(_programId, U_VP_MATRIX)
        _u_m_matrix_location = glGetUniformLocation(_programId, U_M_MATRIX)
    }

    private val floatBuffer = FloatBuffer.allocate(16)

    fun set_vp_matrix(vp_matrix: Mat4) {
        glUniformMatrix4fv(_u_vp_matrix_location, 1,
            false,
            vp_matrix.to(floatBuffer, 0).array()
            , 0)
    }

    fun set_u_matrix(u_matrix: Mat4) {
        glUniformMatrix4fv(_u_m_matrix_location, 1,
            false, u_matrix.to(floatBuffer, 0).array(), 0)
    }

    fun use_program() {
        glUseProgram(_programId)
    }
}
