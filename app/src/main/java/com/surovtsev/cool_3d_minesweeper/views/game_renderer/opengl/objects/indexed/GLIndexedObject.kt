package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.indexed

import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.utils.opengl.buffers.IndexBuffer
import com.surovtsev.cool_3d_minesweeper.utils.opengl.buffers.VertexArray
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.programs.ModelGLSLProgram

open class GLIndexedObject(val modelGlslProgram: ModelGLSLProgram
                           , val coordinates: FloatArray
                           , val indexes: ShortArray) {

    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray = VertexArray(coordinates)
    private val indexBuffer = IndexBuffer(indexes)


    fun bindAttribs() {
        vertexArray.setVertexAttribPointer(0, modelGlslProgram.aPosition.location,
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