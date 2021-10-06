package com.surovtsev.cool3dminesweeper.utils.gles.model.program

import android.content.Context
import android.opengl.GLES20
import com.surovtsev.cool3dminesweeper.R
import com.surovtsev.cool3dminesweeper.utils.gles.helpers.ShaderHelper

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

    companion object {
        private const val uPointSize = "u_pointSize"
    }

    val mAPosition = Attribute(aPositionName)

    private val mUPointSize = Uniform(uPointSize)
    private val mUColor = Uniform(uColorName)
    val mLineWidth = 10f

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