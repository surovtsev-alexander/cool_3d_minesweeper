package com.surovtsev.cool_3d_minesweeper.views.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.buffers.VertexArray
import com.surovtsev.cool_3d_minesweeper.models.gles.programs.CubeGLESProgram
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.TextureHelper
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.ICanUpdateTexture
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.IGLObject

class CubeView(
    context: Context,
    trianglesCoordinates: FloatArray,
    isEmpty: FloatArray,
    textureCoordinates: FloatArray
):
    IGLObject,
    ICanUpdateTexture
{
    val cubeGLESProgram =
        CubeGLESProgram(
            context
        )
    private val textureId = TextureHelper.loadTexture(context, R.drawable.skin)

    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray = VertexArray(trianglesCoordinates)
    val isEmptyArray = VertexArray(isEmpty)
    val textureCoordinatesArray = VertexArray(textureCoordinates)

    init {
        cubeGLESProgram.prepareProgram()
        setTexture()
    }

    override fun updateTexture(pointedCell: PointedCell) {
        val position = pointedCell.index
        val skin = pointedCell.skin
        val id = position.id
        val empty = skin.isEmpty()

        if (empty) {
            val cubeIndexsCount = CubesCoordinatesGenerator.invExtendedIndexedArray.size
            val startPos = cubeIndexsCount * id

            isEmptyArray.updateBuffer(
                FloatArray(cubeIndexsCount) { 1f },
                startPos,
                cubeIndexsCount
            )
        } else {
            val textureIndexesCount =
                TextureCoordinatesHelper.textureToSquareTemplateCoordinates.count()
            val startPos = textureIndexesCount * id * 6

            val xx = arrayOf(
                skin.texture[1],
                skin.texture[2],
                skin.texture[0],
                skin.texture[2],
                skin.texture[0],
                skin.texture[1],
            )


            val resArray = xx.map {
                TextureCoordinatesHelper.textureCoordinates[it]!!.asIterable()
            }.flatten().toFloatArray()
            textureCoordinatesArray.updateBuffer(
                resArray,
                startPos,
                resArray.count()
            )
        }
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