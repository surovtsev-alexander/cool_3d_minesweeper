package com.surovtsev.cool3dminesweeper.utils.gles.helpers

import android.content.Context
import android.opengl.GLES20.*
import android.util.Log
import com.surovtsev.utils.loggerconfig.LoggerConfig
import com.surovtsev.utils.textresourcereader.TextResourceReader

object ShaderHelper {

    data class ShaderLoadParameters(
        val context: Context,
        val vertexShaderResourceId: Int,
        val fragmentShaderResourceId: Int
        )

    private const val TAG = "ShaderHelper"

    private fun compileVertexShader(shaderCode: String) =
        compileShader(GL_VERTEX_SHADER, shaderCode)

    private fun compileFragmentShader(shaderCode: String) =
        compileShader(GL_FRAGMENT_SHADER, shaderCode)

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderObjectId = glCreateShader(type)

        if (shaderObjectId == 0) {
            if (LoggerConfig.LOG_SHADER_COMPILATION) {
                Log.w(TAG, "Could not create new shader")
            }

            return 0
        }

        glShaderSource(shaderObjectId, shaderCode)
        glCompileShader(shaderObjectId)

        val compileStatus = intArrayOf(0)

        glGetShaderiv(
            shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

        if (LoggerConfig.LOG_SHADER_COMPILATION) {
            Log.v(
                TAG, "Result of compiling source:"
                    + "\n" + shaderCode
                    + "\n" + glGetShaderInfoLog(shaderObjectId))
        }

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId)

            if (LoggerConfig.LOG_SHADER_COMPILATION) {
                Log.w(TAG, "Compilation of shader failed.")
            }

            return 0
        }

        return shaderObjectId
    }

    fun linkProgram(params: ShaderLoadParameters) =
        linkProgram(
            compileVertexShader(
                TextResourceReader.readTextFileFromResource(
                params.context, params.vertexShaderResourceId)
            ),
            compileFragmentShader(
                TextResourceReader.readTextFileFromResource(
                params.context, params.fragmentShaderResourceId)
            )
        )

    private fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = glCreateProgram()

        if (programObjectId == 0) {
            if (LoggerConfig.LOG_SHADER_COMPILATION) {
                Log.w(TAG, "Could not create new program")
            }

            return 0
        }

        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)

        glLinkProgram(programObjectId)

        val linkStatus = intArrayOf(0)

        glGetProgramiv(programObjectId, GL_LINK_STATUS
            , linkStatus, 0)

        if (LoggerConfig.LOG_SHADER_COMPILATION) {
            Log.v(
                TAG, "Result of linking program:\n"
                    + glGetProgramInfoLog(programObjectId))
        }

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId)

            if (LoggerConfig.LOG_SHADER_COMPILATION) {
                Log.w(TAG, "Link of program failed.")
            }

            return 0
        }

        return programObjectId
    }

    fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)

        val validateStatus = intArrayOf(0)

        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS
            , validateStatus, 0)

        if (LoggerConfig.LOG_SHADER_COMPILATION) {
            Log.v(
                TAG, "Result of validating program: " + validateStatus[0] + "\n"
                        + glGetProgramInfoLog(programObjectId)
            )
        }

        return validateStatus[0] != 0
    }
}