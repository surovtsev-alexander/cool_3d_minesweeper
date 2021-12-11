package com.surovtsev.cool3dminesweeper.utils.unused.indexedobject

import android.opengl.GLES20.*
import com.surovtsev.game.utils.gles.model.buffers.IndexBuffer
import com.surovtsev.game.utils.gles.model.buffers.VertexArray
import com.surovtsev.cool3dminesweeper.models.gles.programs.CubeGLESProgram

@Suppress("unused")
open class GLIndexedObject(
    private val cubeGLESProgram: CubeGLESProgram,
    val coordinates: FloatArray,
    indexes: IntArray) {

    companion object {
        private const val positionComponentCount = 3
    }

    private val vertexArray = VertexArray().apply {  allocateBuffer(coordinates) }
    private val indexBuffer = IndexBuffer(indexes)


    @Suppress("SpellCheckingInspection")
    fun bindAttribs() {
        vertexArray.setVertexAttribPointer(0, cubeGLESProgram.aPosition.location,
            positionComponentCount, 0)
    }

    fun draw() {
        @Suppress("ConstantConditionIf")
        if (false) {
            glDrawArrays(
                GL_TRIANGLES, 0,
                vertexArray.floatBuffer.capacity() / positionComponentCount
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