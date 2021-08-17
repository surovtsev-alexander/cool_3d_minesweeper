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
        context, R.raw.model_vertex_shader, R.raw.model_fragment_shader)) {

    private val A_TRIANGLE_NUM = "a_TriangleNum"
    private val A_TRIANGLE_TEXTURE = "a_TriangleTexture"
    private val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    private val U_TEXTURE_UNIT = "u_TextureUnit"

    val aPosition = Attribute(A_POSITION)
    val aTriangleNum = Attribute(A_TRIANGLE_NUM)
    val aTriangleTexture = Attribute(A_TRIANGLE_TEXTURE)
    val aTextureCoordinates = Attribute(A_TEXTURE_COORDINATES)
    private val uColor = Uniform(U_COLOR)
    private val u_MVP_Matrix = Uniform(U_MVP_MATRIX)
    val mUTextureLocation = Uniform(U_TEXTURE_UNIT)

    override val fields = arrayOf(
        aPosition,
        aTriangleNum,
        aTriangleTexture,
        aTextureCoordinates,
        uColor,
        u_MVP_Matrix,
        mUTextureLocation
    )

    override fun loadLocations() {
        super.loadLocations()

        glUniform4f(uColor.location, 1f, 0f, 0f, 1f)
    }


    companion object {
        private val floatBuffer = FloatBuffer.allocate(16)
    }

    fun fillU_MVP_Matrix(mvp_matrix: Mat4) {
        glUniformMatrix4fv(u_MVP_Matrix.location, 1,
            false,
            mvp_matrix.to(floatBuffer, 0).array()
            , 0)
    }
}
