package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.programs

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.opengl.helpers.ShaderHelper
import glm_.mat4x4.Mat4
import java.nio.FloatBuffer

open class ModelGLSLProgram(
    context: Context): GLSLProgram(
    ShaderHelper.ShaderLoadParameters(
        context, R.raw.model_vertex_shader, R.raw.model_fragment_shader)) {

    private val A_IS_EMPTY = "a_isEmpty"
    private val A_TEXTURE_COORDINATES = "a_textureCoordinates"
    private val U_TEXTURE_UNIT = "u_textureUnit"

    val aPosition = Attribute(A_POSITION)
    val aIsEmpty = Attribute(A_IS_EMPTY)
    val aTextureCoordinates = Attribute(A_TEXTURE_COORDINATES)
    private val u_MVP = Uniform(U_MVP)
    val mUTextureLocation = Uniform(U_TEXTURE_UNIT)

    override val fields = arrayOf(
        aPosition,
        aIsEmpty,
        aTextureCoordinates,
        u_MVP,
        mUTextureLocation
    )

    override fun loadLocations() {
        super.loadLocations()
    }


    companion object {
        private val floatBuffer = FloatBuffer.allocate(16)
    }

    fun fillU_MVP(mvp_matrix: Mat4) {
        glUniformMatrix4fv(u_MVP.location, 1,
            false,
            mvp_matrix.to(floatBuffer, 0).array()
            , 0)
    }
}
