package com.surovtsev.cool_3d_minesweeper.util

import android.content.Context
import android.opengl.GLES20.*
import android.util.Log

class ShaderHelper {

    companion object {
        val TAG = "ShaderHelper"

        fun compileVertextShader(shaderCode: String) =
            compileShader(GL_VERTEX_SHADER, shaderCode)

        fun compileFragmentShader(shaderCode: String) =
            compileShader(GL_FRAGMENT_SHADER, shaderCode)

        fun compileShader(type: Int, shaderCode: String): Int {
            val shaderObjectId = glCreateShader(type)

            if (shaderObjectId == 0) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Coould not create new shader")
                }

                return 0
            }

            glShaderSource(shaderObjectId, shaderCode)
            glCompileShader(shaderObjectId)

            val compileStatus = intArrayOf(0)

            glGetShaderiv(
                shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

            if (LoggerConfig.ON) {
                Log.v(TAG, "Result of compiling source:"
                        + "\n" + shaderCode
                        + "\n" + glGetShaderInfoLog(shaderObjectId))
            }

            if (compileStatus[0] == 0) {
                glDeleteShader(shaderObjectId)

                if (LoggerConfig.ON) {
                    Log.w(TAG, "Compilation of shader failed.")
                }

                return 0
            }

            return shaderObjectId
        }

        fun linkProgram(context: Context, vertexShaderId: Int, fragmentShaderId: Int) =
            Companion.linkProgram(
                compileVertextShader(TextResourceReader.readTextFileFromResource(
                    context, vertexShaderId)),
                compileFragmentShader(TextResourceReader.readTextFileFromResource(
                    context, fragmentShaderId))
            )

        fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
            val programObjectId = glCreateProgram()

            if (programObjectId == 0) {
                if (LoggerConfig.ON) {
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

            if (LoggerConfig.ON) {
                Log.v(TAG, "Result of linking program:\n"
                        + glGetProgramInfoLog(programObjectId))
            }

            if (linkStatus[0] == 0) {
                glDeleteProgram(programObjectId)

                if (LoggerConfig.ON) {
                    Log.w(TAG, "Link of program failed.")
                }

                return 0
            }

            return programObjectId
        }

        fun validateProgram(programObjecId: Int): Boolean {
            glValidateProgram(programObjecId)

            val validateStatus = intArrayOf(0)

            glGetProgramiv(programObjecId, GL_VALIDATE_STATUS
                , validateStatus, 0)

            Log.v(TAG, "Result of validating program: " + validateStatus[0] + "\n"
                    + glGetProgramInfoLog(programObjecId))

            return validateStatus[0] != 0
        }
    }
}