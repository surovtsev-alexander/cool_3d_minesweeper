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

import android.opengl.GLES20
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUseProgram
import com.surovtsev.utils.gles.helpers.ShaderHelper
import com.surovtsev.utils.loggerconfig.UtilsLoggerConfig
import glm_.mat4x4.Mat4
import java.nio.FloatBuffer


abstract class GLESProgram (
    private val shaderLoadParameters: ShaderHelper.ShaderLoadParameters
) {
    protected var programId = 0
        private set

    companion object {
        const val aPositionName = "a_position"
        const val uColorName = "u_color"
        private const val uMVPName = "u_MVP"

        private var mvpBuffer: FloatBuffer? = null
    }

    private val uMVP = Uniform(uMVPName)

    abstract val fields: List<GLESField>

    fun prepareProgram() {
        loadProgram()
        useProgram()
        loadLocations()
        createMVPBuffer()
    }

    private fun loadProgram() {
        programId = ShaderHelper.linkProgram(shaderLoadParameters)

        if (UtilsLoggerConfig.ON) {
            ShaderHelper.validateProgram(programId)
        }
    }

    fun useProgram() {
        glUseProgram(programId)
    }

    open fun loadLocations() {
        uMVP.getLocation()

        fields.forEach { it.getLocation() }
    }

    private fun createMVPBuffer() {
        mvpBuffer = FloatBuffer.allocate(16)
    }

    fun fillMVP(mvpMatrix: Mat4) {
        GLES20.glUniformMatrix4fv(
            uMVP.location, 1,
            false,
            mvpMatrix.to(mvpBuffer!!.array(), 0)
            , 0
        )
    }

    abstract inner class GLESField(val name: String, var location: Int = 0) {
        abstract fun getLocation()
    }

    inner class Attribute(name: String, location: Int = 0): GLESField(name, location)  {
        override fun getLocation() {
            location = glGetAttribLocation(programId, name)
        }
    }

    inner class Uniform(name: String, location: Int = 0): GLESField(name, location) {
        override fun getLocation() {
            location = glGetUniformLocation(programId, name)
        }
    }
}