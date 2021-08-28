package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.programs

import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUseProgram
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.ShaderHelper
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig


abstract class GLSLProgram(val shaderLoadParameters: ShaderHelper.ShaderLoadParameters) {
    protected var programId = 0
        private set

    protected val A_POSITION = "a_position"
    protected val U_COLOR  = "u_color"
    protected val U_MVP = "u_MVP"

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