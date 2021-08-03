package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.ModelGLSLProgram
import com.surovtsev.cool_3d_minesweeper.util.TextureHelper

class ModelObject(
    context: Context,
    val trianglesCoordinates: FloatArray,
    val trianglesNums: FloatArray,
    val trianglesTextures: FloatArray,
    val textureCoordinates: FloatArray): IGLObject
{
    val mModelModelGLSLProgram: ModelGLSLProgram
    private var mTextureId = 0

    init {
        mModelModelGLSLProgram = ModelGLSLProgram(context)
        mModelModelGLSLProgram.load_program()
        mModelModelGLSLProgram.use_program()
        mModelModelGLSLProgram.load_locations()

        mTextureId = TextureHelper.loadTexture(context, R.drawable.texture_1)
    }

    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray = VertexArray(trianglesCoordinates)
    private val numsArray = VertexArray(trianglesNums)
    private val texturesArray = VertexArray(trianglesTextures)
    private val textureCoordinatesArray = VertexArray(textureCoordinates)

    override fun bind_attributes() {
        vertexArray.setVertexAttribPointer(0, mModelModelGLSLProgram.mAPosition.location,
            POSITION_COMPONENT_COUNT, 0)
        numsArray.setVertexAttribPointer(0, mModelModelGLSLProgram.mATriangleNum.location,
            1, 0)
        texturesArray.setVertexAttribPointer(0, mModelModelGLSLProgram.mATriangleTexture.location,
            1, 0)
        textureCoordinatesArray.setVertexAttribPointer(0,
            mModelModelGLSLProgram.mATextureCoordinates.location, 2, 0)
    }

    fun set_texture() {
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