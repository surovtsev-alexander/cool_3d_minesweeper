package com.surovtsev.cool3dminesweeper.utils.gles.model.program

import android.opengl.GLES20
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUseProgram
import com.surovtsev.cool3dminesweeper.utils.gles.helpers.ShaderHelper
import com.surovtsev.cool3dminesweeper.utils.logger_config.LoggerConfig
import glm_.mat4x4.Mat4
import java.nio.FloatBuffer


/* TODO: move to Dagger */

abstract class GLESProgram(
    private val shaderLoadParameters: ShaderHelper.ShaderLoadParameters
    ) {
    protected var programId = 0
        private set

    companion object {
        const val aPositionName = "a_position"
        const val uColorName = "u_color"
        private const val uMVPName = "u_MVP"

        private val floatBuffer = FloatBuffer.allocate(16)
    }

    private val uMVP = Uniform(uMVPName)

    abstract val fields: Array<GLESField>

    fun prepareProgram() {
        loadProgram()
        useProgram()
        loadLocations()
    }

    private fun loadProgram() {
        programId = ShaderHelper.linkProgram(shaderLoadParameters)

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(programId)
        }
    }

    fun useProgram() {
        glUseProgram(programId)
    }

    open fun loadLocations() {
        uMVP.getLocation()

        fields.forEach { it.getLocation() }

        /*
        if (LoggerConfig.LOG_SHADER_FIELDS_LOCATIONS) {
            Log.d("TEST", "{")
            fields.forEach {
                Log.d("TEST", "${it.name}: ${it.location}")
            }
            Log.d("TEST", "}")
        }
         */
    }

    fun fillMVP(mvpMatrix: Mat4) {
        GLES20.glUniformMatrix4fv(
            uMVP.location, 1,
            false,
            mvpMatrix.to(floatBuffer, 0).array()
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