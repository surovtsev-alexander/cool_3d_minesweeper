package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects

import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.IndexBuffer
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program

open class IndexedObject(val glslProgram: GLSL_Program
                         , val coordinates: FloatArray
                         , val indexes: ShortArray) {

    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray: VertexArray
    private val indexBuffer: IndexBuffer


    init {
        vertexArray = VertexArray(coordinates)
        indexBuffer = IndexBuffer(indexes)
    }

    fun bind_attribs() {
        vertexArray.setVertexAttribPointer(0, glslProgram._a_position_location,
            POSITION_COMPONENT_COUNT, 0)
    }

    fun draw() {
        if (false) {
            glDrawArrays(
                GL_TRIANGLES, 0,
                vertexArray.floatBuffer.capacity() / POSITION_COMPONENT_COUNT
            )
        } else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bufferId)
            glDrawElements(
                GL_TRIANGLES, indexBuffer.vertexData.size,
                GL_UNSIGNED_SHORT, 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        }
    }
}