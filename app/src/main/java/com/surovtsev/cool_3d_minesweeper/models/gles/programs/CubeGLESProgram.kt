package com.surovtsev.cool_3d_minesweeper.models.gles.programs

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.ShaderHelper
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.program.GLESProgram
import java.nio.FloatBuffer

open class CubeGLESProgram(
    context: Context
):
    GLESProgram(
        ShaderHelper.ShaderLoadParameters(
            context,
            R.raw.model_vertex_shader,
            R.raw.model_fragment_shader
        )
    )
{
    private val A_IS_EMPTY = "a_isEmpty"
    private val A_TEXTURE_COORDINATES = "a_textureCoordinates"
    private val U_TEXTURE_UNIT = "u_textureUnit"

    val aPosition = Attribute(A_POSITION)
    val aIsEmpty = Attribute(A_IS_EMPTY)
    val aTextureCoordinates = Attribute(A_TEXTURE_COORDINATES)
    val mUTextureLocation = Uniform(U_TEXTURE_UNIT)

    override val fields = arrayOf(
        aPosition,
        aIsEmpty,
        aTextureCoordinates,
        mUTextureLocation
    )

    companion object {
        private val floatBuffer = FloatBuffer.allocate(16)
    }
}
