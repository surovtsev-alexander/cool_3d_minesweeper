@file:Suppress("MemberVisibilityCanBePrivate")

package com.surovtsev.cool_3d_minesweeper.utils.gles.model.buffers

import android.opengl.GLES20
import com.surovtsev.cool_3d_minesweeper.utils.constants.Constants
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

/* TODO: move to Dagger */
@Suppress("unused")
class GLIntBuffer (data: IntArray) {
// Bind to the buffer.

// Transfer data to native memory.

// Transfer data from native memory to the GPU buffer.

// IMPORTANT: Unbind from the buffer when we're done with it.

    val bufferId: Int
    val dataBuffer: IntBuffer

    init {
        val buffers = intArrayOf(0)
        GLES20.glGenBuffers(buffers.count(), buffers, 0)
        if (buffers[0] == 0) {
            throw RuntimeException(
                "Could not create a new vertex buffer object.")
        }
        bufferId = buffers[0]
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferId)
        dataBuffer = ByteBuffer
            .allocateDirect(data.count() * Constants.BytesPerInt)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .put(data)
        dataBuffer.position(0)
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            dataBuffer.capacity() * Constants.BytesPerInt,
            dataBuffer, GLES20.GL_STATIC_DRAW
        )
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    fun setVertexAttribPointer(
        dataOffset: Int, attributeLocation: Int,
        componentCount: Int, stride: Int) {
        dataBuffer.position(dataOffset)
        GLES20.glVertexAttribPointer(
            attributeLocation, componentCount, GLES20.GL_INT,
            false, stride, dataBuffer
        )
        GLES20.glEnableVertexAttribArray(attributeLocation)

        dataBuffer.position(0)
    }
}