package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects

import android.opengl.GLES20
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program

class Triangle(val glslProgram: GLSL_Program) {

    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray: VertexArray

    private val _trianglesCoordinates = floatArrayOf(
        -1f, -1f, 0f,
        1f, -1f, 0f,
        0f, 1f, 0f
    )

    private val _indexes = intArrayOf(
        0, 1, 2
    )

    init {
        vertexArray = VertexArray(_trianglesCoordinates)
    }

    fun bind_attribs() {
        vertexArray.setVertexAttribPointer(0, glslProgram._a_position_location,
            POSITION_COMPONENT_COUNT, 0)
    }

    fun draw() {
        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES, 0,
            _trianglesCoordinates.count() / POSITION_COMPONENT_COUNT
        )
    }
}