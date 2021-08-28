package com.surovtsev.cool_3d_minesweeper.utils.gles.model.program

import android.content.Context
import android.opengl.GLES20
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.ShaderHelper
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.program.GLESProgram

open class PointerGLESProgram(
    context: Context
):
    GLESProgram(
        ShaderHelper.ShaderLoadParameters(
            context,
            R.raw.pointer_vertex_shader,
            R.raw.pointer_fragment_shader
        )
) {

    private val U_POINT_SIZE = "u_pointSize"

    val mAPosition = Attribute(A_POSITION)

    private val mUPointSize = Uniform(U_POINT_SIZE)
    private val mUColor = Uniform(U_COLOR)
    val mLineWidth = 10f;

    override val fields = arrayOf(
        mAPosition,
        mUPointSize,
        mUColor,
    )

    override fun loadLocations() {
        super.loadLocations()

        GLES20.glUniform1f(mUPointSize.location, mLineWidth)
        GLES20.glUniform4f(mUColor.location, 1f, 0f, 0f, 1f)
    }
}