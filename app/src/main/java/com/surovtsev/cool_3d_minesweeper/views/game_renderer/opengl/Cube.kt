package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.buffers.VertexArray
import com.surovtsev.cool_3d_minesweeper.models.gles.programs.CubeGLESProgram
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.TextureHelper
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.IGLObject

class Cube(
    context: Context,
    trianglesCoordinates: FloatArray,
    isEmpty: FloatArray,
    textureCoordinates: FloatArray):
    IGLObject
{
    val cubeGLESProgram: CubeGLESProgram
    private var textureId = 0

    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray = VertexArray(trianglesCoordinates)
    val isEmptyArray = VertexArray(isEmpty)
    val textureCoordinatesArray = VertexArray(textureCoordinates)

    init {
        cubeGLESProgram =
            CubeGLESProgram(
                context
            )
        cubeGLESProgram.prepareProgram()

        textureId = TextureHelper.loadTexture(context, R.drawable.skin)
        setTexture()
    }

    override fun bindData() {
        vertexArray.setVertexAttribPointer(0, cubeGLESProgram.aPosition.location,
            POSITION_COMPONENT_COUNT, 0)
        isEmptyArray.setVertexAttribPointer(0, cubeGLESProgram.aIsEmpty.location,
            1, 0)
        textureCoordinatesArray.setVertexAttribPointer(0,
            cubeGLESProgram.aTextureCoordinates.location, 2, 0)
    }

    fun setTexture() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(cubeGLESProgram.mUTextureLocation.location, 0)
    }

    override fun draw() {
        glDrawArrays(
            GL_TRIANGLES, 0,
            vertexArray.floatBuffer.capacity() / POSITION_COMPONENT_COUNT
        )
    }
}