package com.surovtsev.cool_3d_minesweeper.utils.gles.model.buffers

import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.utils.constants.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray {
    val floatBuffer: FloatBuffer

    constructor(vertexData: FloatArray) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.count() * Constants.BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
    }

    fun setVertexAttribPointer(
        dataOffset: Int, attributeLocation: Int,
        componentCount: Int, stride: Int) {
        floatBuffer.position(dataOffset)
        glVertexAttribPointer(
            attributeLocation, componentCount, GL_FLOAT,
            false, stride, floatBuffer)
        glEnableVertexAttribArray(attributeLocation)

        floatBuffer.position(0)
    }


    fun updateBuffer(vertexData: FloatArray, start: Int, count: Int) {
        floatBuffer.position(start)
        floatBuffer.put(vertexData, 0, count)
        floatBuffer.position(0)
    }

    fun updateBuffer(vertexData: FloatArray, start: Int) {
        floatBuffer.position(start)
        floatBuffer.put(vertexData, 0, vertexData.count())
        floatBuffer.position(0)
    }
}
