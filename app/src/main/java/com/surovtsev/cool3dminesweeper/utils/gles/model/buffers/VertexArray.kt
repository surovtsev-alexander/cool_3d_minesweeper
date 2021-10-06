package com.surovtsev.cool3dminesweeper.utils.gles.model.buffers

import android.opengl.GLES20.*
import com.surovtsev.cool3dminesweeper.utils.constants.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/* TODO: move to Dagger */

class VertexArray(vertexData: FloatArray) {
    val floatBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertexData.count() * Constants.BytesPerFloat)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(vertexData)

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
