package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects

import android.opengl.GLES20
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.GLIntBuffer
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program

class GLObject(val glslProgram: GLSL_Program,
               val trianglesCoordinates: FloatArray,
               val trianglesNums: FloatArray,
               val trianglesTextures: FloatArray) {


    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray = VertexArray(trianglesCoordinates)
    private val numsArray = VertexArray(trianglesNums)
    private val texturesArray = VertexArray(trianglesTextures)

    fun bind_attribs() {
        vertexArray.setVertexAttribPointer(0, glslProgram._a_position_location,
            POSITION_COMPONENT_COUNT, 0)
        numsArray.setVertexAttribPointer(0, glslProgram._a_triangle_num,
            1, 0)
        texturesArray.setVertexAttribPointer(0, glslProgram._a_triangle_texture,
            1, 0)
    }

    fun draw() {
        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES, 0,
            vertexArray.floatBuffer.capacity() / POSITION_COMPONENT_COUNT
        )
    }
}