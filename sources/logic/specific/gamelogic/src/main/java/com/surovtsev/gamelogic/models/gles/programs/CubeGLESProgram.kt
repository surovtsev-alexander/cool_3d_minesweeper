/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.models.gles.programs

import android.content.Context
import com.surovtsev.gamelogic.R
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.utils.gles.model.program.GLESProgram
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

    override val fields = listOf(
        aPosition,
        aIsEmpty,
        aTextureCoordinates,
        mUTextureLocation
    )
}
