package com.surovtsev.cool_3d_minesweeper.gl_helpers.program

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.gl_helpers.helpers.ShaderHelper
import glm_.mat4x4.Mat4
import java.nio.FloatBuffer

open class ModelGLSLProgram(
    context: Context): GLSLProgram(
    ShaderHelper.ShaderLoadParameters(
        context, R.raw.scene_vertex_shader, R.raw.scene_fragment_shader)) {

    private val A_TRIANGLE_NUM = "a_TriangleNum"
    private val A_TRIANGLE_TEXTURE = "a_TriangleTexture"
    private val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    private val U_TEXTURE_UNIT = "u_TextureUnit"

    val mAPosition = Attribute(A_POSITION)
    val mATriangleNum = Attribute(A_TRIANGLE_NUM)
    val mATriangleTexture = Attribute(A_TRIANGLE_TEXTURE)
    val mATextureCoordinates = Attribute(A_TEXTURE_COORDINATES)
    private val mUColor = Uniform(U_COLOR)
    private val mU_MVP_Matrix = Uniform(U_MVP_MATRIX)
    val mUTextureLocation = Uniform(U_TEXTURE_UNIT)

    override val fields = arrayOf(
        mAPosition,
        mATriangleNum,
        mATriangleTexture,
        mATextureCoordinates,
        mUColor,
        mU_MVP_Matrix,
        mUTextureLocation
    )

    override fun load_locations() {
        super.load_locations()

        glUniform4f(mUColor.location, 1f, 0f, 0f, 1f)
    }


    companion object {
        private val floatBuffer = FloatBuffer.allocate(16)
    }

    fun fillU_MVP_Matrix(mvp_matrix: Mat4) {
        glUniformMatrix4fv(mU_MVP_Matrix.location, 1,
            false,
            mvp_matrix.to(floatBuffer, 0).array()
            , 0)
    }
}
