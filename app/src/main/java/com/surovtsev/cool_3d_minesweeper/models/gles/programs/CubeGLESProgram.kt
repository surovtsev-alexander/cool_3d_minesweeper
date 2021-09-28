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
    companion object {
        private const val aIsEmptyName = "a_isEmpty"
        private const val aTextureCoordinatesName = "a_textureCoordinates"
        private const val uTextureUnitName = "u_textureUnit"
    }

    val aPosition = Attribute(aPositionName)
    val aIsEmpty = Attribute(aIsEmptyName)
    val aTextureCoordinates = Attribute(aTextureCoordinatesName)
    val mUTextureLocation = Uniform(uTextureUnitName)

    override val fields = arrayOf(
        aPosition,
        aIsEmpty,
        aTextureCoordinates,
        mUTextureLocation
    )
}
