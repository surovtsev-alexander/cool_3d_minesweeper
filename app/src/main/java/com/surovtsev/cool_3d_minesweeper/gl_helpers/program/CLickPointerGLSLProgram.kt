package com.surovtsev.cool_3d_minesweeper.gl_helpers.program

import android.content.Context
import android.opengl.GLES20
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.gl_helpers.helpers.ShaderHelper

class CLickPointerGLSLProgram(
    context: Context): GLSLProgram(
    ShaderHelper.ShaderLoadParameters(
    context, R.raw.pointer_vertex_shader, R.raw.pointer_fragment_shader
)) {

    private val U_POINT_SIZE = "u_PointSize"

    val mAPosition = Attribute(A_POSITION)

    private val mUPointSize = Uniform(U_POINT_SIZE)
    private val mUColor = Uniform(U_COLOR)
    val mU_VP_Matrix = Uniform(U_VP_MATRIX)

    override val fields = arrayOf(
        mAPosition,
        mUPointSize,
        mUColor,
        mU_VP_Matrix
    )

    override fun load_locations() {
        super.load_locations()

        GLES20.glUniform1f(mUPointSize.location, 10f)
        GLES20.glUniform4f(mUColor.location, 1f, 0f, 0f, 1f)
    }
}