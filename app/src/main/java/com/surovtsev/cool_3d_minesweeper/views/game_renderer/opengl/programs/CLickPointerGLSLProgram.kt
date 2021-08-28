package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.programs

import android.content.Context
import android.opengl.GLES20
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.opengl.helpers.ShaderHelper
import glm_.mat4x4.Mat4
import java.nio.FloatBuffer

open class CLickPointerGLSLProgram(
    context: Context): GLSLProgram(
    ShaderHelper.ShaderLoadParameters(
    context, R.raw.pointer_vertex_shader, R.raw.pointer_fragment_shader
)) {

    private val U_POINT_SIZE = "u_pointSize"

    val mAPosition = Attribute(A_POSITION)

    private val mUPointSize = Uniform(U_POINT_SIZE)
    private val mUColor = Uniform(U_COLOR)
    val mU_MVP_Matrix = Uniform(U_MVP)
    val mLineWidth = 10f;

    override val fields = arrayOf(
        mAPosition,
        mUPointSize,
        mUColor,
        mU_MVP_Matrix
    )

    override fun loadLocations() {
        super.loadLocations()

        GLES20.glUniform1f(mUPointSize.location, mLineWidth)
        GLES20.glUniform4f(mUColor.location, 1f, 0f, 0f, 1f)
    }

    companion object {
        private val floatBuffer = FloatBuffer.allocate(16)
    }

    fun fillU_MVP(mvpMatrix: Mat4) {
        GLES20.glUniformMatrix4fv(
            mU_MVP_Matrix.location, 1,
            false,
            mvpMatrix.to(floatBuffer, 0).array()
            , 0
        )
    }
}