package com.surovtsev.cool_3d_minesweeper.gl_helpers.program

import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUseProgram
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.helpers.ShaderHelper
import com.surovtsev.cool_3d_minesweeper.utils.LoggerConfig


abstract class GLSLProgram(val shaderLoadParameters: ShaderHelper.ShaderLoadParameters) {
    protected var programId = 0
        private set

    protected val A_POSITION = "a_Position"
    protected val U_COLOR  = "u_Color"
    protected val U_MVP_MATRIX = "u_MVP_Matrix"

    abstract val fields: Array<GLSLField>

    fun prepare_program() {
        load_program()
        use_program()
        loadLocations()
    }

    fun load_program() {
        programId = ShaderHelper.linkProgram(shaderLoadParameters)

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(programId)
        }
    }

    fun use_program() {
        glUseProgram(programId)
    }

    open fun loadLocations() {
        fields.forEach { it.get_location() }

        if (LoggerConfig.LOG_SHADER_FIELDS_LOCATIONS) {
            Log.d("TEST", "{")
            fields.forEach {
                Log.d("TEST", "${it.name}: ${it.location}")
            }
            Log.d("TEST", "}")
        }
    }

    abstract inner class GLSLField(val name: String, var location: Int = 0) {
        abstract fun get_location()
    }

    inner class Attribute(name: String, location: Int = 0): GLSLField(name, location)  {
        override fun get_location() {
            location = glGetAttribLocation(programId, name)
        }
    }

    inner class Uniform(name: String, location: Int = 0): GLSLField(name, location) {
        override fun get_location() {
            location = glGetUniformLocation(programId, name)
        }
    }
}