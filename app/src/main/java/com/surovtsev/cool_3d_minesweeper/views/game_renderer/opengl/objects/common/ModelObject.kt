package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.common

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.opengl.buffers.VertexArray
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.programs.ModelGLSLProgram
import com.surovtsev.cool_3d_minesweeper.utils.opengl.helpers.TextureHelper
import com.surovtsev.cool_3d_minesweeper.utils.opengl.interfaces.IGLObject

class ModelObject(
    context: Context,
    trianglesCoordinates: FloatArray,
    isEmpty: FloatArray,
    textureCoordinates: FloatArray):
    IGLObject
{
    val modelModelGLSLProgram: ModelGLSLProgram
    private var textureId = 0

    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray = VertexArray(trianglesCoordinates)
    val isEmptyArray = VertexArray(isEmpty)
    val textureCoordinatesArray = VertexArray(textureCoordinates)

    init {
        modelModelGLSLProgram = ModelGLSLProgram(context)
        modelModelGLSLProgram.prepare_program()

        textureId = TextureHelper.loadTexture(context, R.drawable.skin)
        setTexture()
    }

    override fun bindData() {
        vertexArray.setVertexAttribPointer(0, modelModelGLSLProgram.aPosition.location,
            POSITION_COMPONENT_COUNT, 0)
        isEmptyArray.setVertexAttribPointer(0, modelModelGLSLProgram.aIsEmpty.location,
            1, 0)
        textureCoordinatesArray.setVertexAttribPointer(0,
            modelModelGLSLProgram.aTextureCoordinates.location, 2, 0)
    }

    fun setTexture() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(modelModelGLSLProgram.mUTextureLocation.location, 0)
    }

    override fun draw() {
        glDrawArrays(
            GL_TRIANGLES, 0,
            vertexArray.floatBuffer.capacity() / POSITION_COMPONENT_COUNT
        )
    }
}