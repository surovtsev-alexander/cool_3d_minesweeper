package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram
import com.surovtsev.cool_3d_minesweeper.utils.TextureHelper

class ModelObject(
    context: Context,
    val trianglesCoordinates: FloatArray,
    trianglesNums: FloatArray,
    trianglesTextures: FloatArray,
    textureCoordinates: FloatArray): IGLObject
{
    val mModelModelGLSLProgram: ModelGLSLProgram
    private var mTextureId = 0

    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray = VertexArray(trianglesCoordinates)
    private val numsArray = VertexArray(trianglesNums)
    val texturesArray = VertexArray(trianglesTextures)
    private val textureCoordinatesArray = VertexArray(textureCoordinates)

    init {
        mModelModelGLSLProgram = ModelGLSLProgram(context)
        mModelModelGLSLProgram.prepare_program()

        mTextureId = TextureHelper.loadTexture(context, R.drawable.texture_1)
        setTexture()
    }

    override fun bindData() {
        vertexArray.setVertexAttribPointer(0, mModelModelGLSLProgram.aPosition.location,
            POSITION_COMPONENT_COUNT, 0)
        numsArray.setVertexAttribPointer(0, mModelModelGLSLProgram.aTriangleNum.location,
            1, 0)
        texturesArray.setVertexAttribPointer(0, mModelModelGLSLProgram.aTriangleTexture.location,
            1, 0)
        textureCoordinatesArray.setVertexAttribPointer(0,
            mModelModelGLSLProgram.aTextureCoordinates.location, 2, 0)
    }

    fun setTexture() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, mTextureId)
        glUniform1i(mModelModelGLSLProgram.mUTextureLocation.location, 0)
    }

    override fun draw() {
        glDrawArrays(
            GL_TRIANGLES, 0,
            vertexArray.floatBuffer.capacity() / POSITION_COMPONENT_COUNT
        )
    }
}