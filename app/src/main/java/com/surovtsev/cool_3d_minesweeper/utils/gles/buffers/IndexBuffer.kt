package com.surovtsev.cool_3d_minesweeper.utils.gles.buffers

import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.utils.constants.Constants
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class IndexBuffer(val vertexData: ShortArray) {
    val bufferId: Int

    init {
        val buffers = intArrayOf(0)

        glGenBuffers(buffers.count(), buffers, 0)

        if (buffers[0] == 0) {
            throw RuntimeException(
                "Could not create a new vertex buffer object.")
        }
        bufferId = buffers[0]

        // Bind to the buffer.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0])

        // Transfer data to native memory.
        val vertexArray = ByteBuffer
            .allocateDirect(vertexData.count() * Constants.BYTES_PER_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(vertexData)
        vertexArray.position(0)

        // Transfer data from native memory to the GPU buffer.
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER,
            vertexArray.capacity() * Constants.BYTES_PER_SHORT,
            vertexArray, GL_STATIC_DRAW)

        // IMPORTANT: Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    @Deprecated("unused function")
    fun setVertexAttribPointer(
        dataOffset: Int,
        attributeLocation: Int,
        componentCount: Int,
        stride: Int) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId)
        glVertexAttribPointer(attributeLocation,
            componentCount, GL_SHORT,
            false, stride,
            dataOffset)
        glEnableVertexAttribArray(attributeLocation)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}
