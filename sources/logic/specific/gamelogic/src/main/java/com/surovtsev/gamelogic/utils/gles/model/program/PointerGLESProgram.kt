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


package com.surovtsev.gamelogic.utils.gles.model.program

import android.content.Context
import android.opengl.GLES20
import com.surovtsev.gamelogic.R
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.utils.gles.helpers.ShaderHelper
import javax.inject.Inject

@GameScope
open class PointerGLESProgram @Inject constructor(
    private val context: Context
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

    override val fields = listOf(
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