/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.utils.utils.unused.indexedobject

import android.opengl.GLES20.*
import com.surovtsev.gamelogic.models.gles.programs.CubeGLESProgram
import com.surovtsev.gamelogic.utils.gles.model.buffers.IndexBuffer
import com.surovtsev.gamelogic.utils.gles.model.buffers.VertexArray

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