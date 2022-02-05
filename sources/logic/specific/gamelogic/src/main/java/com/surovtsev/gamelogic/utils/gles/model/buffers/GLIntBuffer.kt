@file:Suppress("MemberVisibilityCanBePrivate")

package com.surovtsev.gamelogic.utils.gles.model.buffers

import android.opengl.GLES20
import com.surovtsev.utils.constants.Constants
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

@Suppress("unused")
class GLIntBuffer (data: IntArray) {
    val bufferId: Int
    val dataBuffer: IntBuffer

    init {
        val buffers = intArrayOf(0)

        GLES20.glGenBuffers(buffers.count(), buffers, 0)

        if (buffers[0] == 0) {
            throw RuntimeException(
                "Could not create a new vertex buffer object."
            )
        }
        bufferId = buffers[0]

        // Bind to the buffer.
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferId)

        // Transfer data to native memory.
        dataBuffer = ByteBuffer
            .allocateDirect(data.count() * Constants.BytesPerFloat)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .put(data)
        dataBuffer.position(0)

        // Transfer data from native memory to the GPU buffer.
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            dataBuffer.capacity() * Constants.BytesPerInt,
            dataBuffer, GLES20.GL_STATIC_DRAW
        )

        // IMPORTANT: Unbind from the buffer when we're done with it.
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