package com.surovtsev.cool_3d_minesweeper.views.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.CubeViewDataHelper
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.PointedCell
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.buffers.VertexArray
import com.surovtsev.cool_3d_minesweeper.models.gles.programs.CubeGLESProgram
import com.surovtsev.cool_3d_minesweeper.utils.gles.helpers.TextureHelper
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.ICanUpdateTexture
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.IGLObject

class CubeView(
    context: Context,
    cubeCoordinates: CubeCoordinates
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

    private val vertexArray: VertexArray
    private val isEmptyArray: VertexArray
    private val textureCoordinatesArray: VertexArray

    init {
        val cubeViewDataHelper = CubeViewDataHelper.createObject(
            cubeCoordinates
        )

        vertexArray = VertexArray(cubeViewDataHelper.triangleCoordinates)
        isEmptyArray = VertexArray(cubeViewDataHelper.isEmpty)
        textureCoordinatesArray = VertexArray(cubeViewDataHelper.textureCoordinates)


        cubeGLESProgram.prepareProgram()
        setTexture()
    }

    companion object {
        private val cubeIndexesCount =
            CubeCoordinates.invExtendedIndexedArray.size
        private val textureIndexesCount =
            TextureCoordinatesHelper.textureToSquareTemplateCoordinates.count() * 6

        private val onesEmpty = FloatArray(cubeIndexesCount) { 1f }
        private val zerosEmpty = FloatArray(cubeIndexesCount) { 0f }
    }

    override fun updateTexture(pointedCell: PointedCell) {
        val position = pointedCell.index
        val skin = pointedCell.skin
        val id = position.id
        val empty = skin.isEmpty()

        if (empty) {
            val startPos = cubeIndexesCount * id

            isEmptyArray.updateBuffer(
                onesEmpty,
                startPos
            )
        } else {
            val startPos = textureIndexesCount * id

            val resArray = TextureCoordinatesHelper.getTextureCoordinates(skin.texture)
            textureCoordinatesArray.updateBuffer(
                resArray,
                startPos
            )
        }
    }

    fun updateTexture(cubeSkin: CubeSkin) {
        val cubesCount = cubeSkin.cubesCount

        val emptyCubes =
            FloatArray(cubeIndexesCount * cubesCount) { 0f }
        val textureCoordinates =
            FloatArray(textureIndexesCount * cubesCount)

        cubeSkin.iterateCubes { xyz ->
            val skin = xyz.getValue(cubeSkin.skins)
            val id = xyz.id

            if (skin.isEmpty()) {
                onesEmpty.copyInto(
                    emptyCubes,
                    cubeIndexesCount * id
                )
            } else {
                skin.getTextureCoordinates().copyInto(
                    textureCoordinates,
                    textureIndexesCount * id
                )
            }
        }

        isEmptyArray.updateBuffer(
            emptyCubes, 0
        )
        textureCoordinatesArray.updateBuffer(
            textureCoordinates, 0
        )
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