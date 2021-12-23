package com.surovtsev.gamescreen.models.gles.programs

import android.content.Context
import com.surovtsev.gamescreen.R
import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.utils.gles.model.program.GLESProgram
import com.surovtsev.utils.gles.helpers.ShaderHelper
import javax.inject.Inject

@GameScope
open class CubeGLESProgram @Inject constructor(
    private val context: Context
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