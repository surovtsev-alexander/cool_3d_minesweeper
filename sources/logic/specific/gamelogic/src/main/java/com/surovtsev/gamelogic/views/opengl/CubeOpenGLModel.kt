/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.views.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.helpers.gamelogic.TextureCoordinatesHelper
import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.gamelogic.R
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.helpers.CubeViewDataHelper
import com.surovtsev.gamelogic.models.gles.programs.CubeGLESProgram
import com.surovtsev.gamelogic.utils.gles.model.buffers.VertexArray
import com.surovtsev.gamelogic.utils.utils.gles.OpenGLModel
import com.surovtsev.gamelogic.utils.utils.gles.TextureUpdater
import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.utils.gles.helpers.TextureHelper
import javax.inject.Inject

@GameScope
class CubeOpenGLModel @Inject constructor(
    private val context: Context,
    private val gameStateHolder: GameStateHolder,
    val cubeGLESProgram: CubeGLESProgram,
):
    OpenGLModel(cubeGLESProgram),
    TextureUpdater
{
    private var textureId: Int = -1

    /* TODO: refactor */
    private var vertexArray: VertexArray = VertexArray()
    private var isEmptyArray: VertexArray = VertexArray()
    private var textureCoordinatesArray: VertexArray = VertexArray()

    fun onSurfaceCreated() {
        gameStateHolder.gameStateFlow.value?.let {
            updateBuffers(
                it.cubeInfo.cubeCoordinates
            )
        }
    }

    private fun updateBuffers(
        cubeCoordinates: CubeCoordinates,
    ) {
        if (textureId != -1) {
            TextureHelper.deleteTexture(textureId)
            textureId = -1
        }
        textureId = TextureHelper.loadTexture(context, R.drawable.skin_downscaled)

        val cubeViewDataHelper = CubeViewDataHelper.createObject(
            cubeCoordinates
        )

        vertexArray.allocateBuffer(cubeViewDataHelper.triangleCoordinates)
        isEmptyArray.allocateBuffer(cubeViewDataHelper.isEmpty)
        textureCoordinatesArray.allocateBuffer(cubeViewDataHelper.textureCoordinates)

        cubeGLESProgram.prepareProgram()
        setTexture()
    }

    companion object {
        private const val positionComponentCount = 3
        private val cubeIndexesCount =
            CubeCoordinates.invExtendedIndexedArray.size
        private val textureIndexesCount =
            TextureCoordinatesHelper.textureToSquareTemplateCoordinates.count() * 6

        private val onesEmpty = FloatArray(cubeIndexesCount) { 1f }
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

    fun updateTexture(gameState: GameState) {
        val gameConfig = gameState.gameConfig
        val cellsCount = gameConfig.cellsCount

        val emptyCubes =
            FloatArray(cubeIndexesCount * cellsCount) { 0f }
        val textureCoordinates =
            FloatArray(textureIndexesCount * cellsCount)

        gameState.cubeInfo.cubeSkin.skinsWithIndexes.forEach { (skin, cellIndex) ->
            val id = cellIndex.id

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
            positionComponentCount, 0)
        isEmptyArray.setVertexAttribPointer(0, cubeGLESProgram.aIsEmpty.location,
            1, 0)
        textureCoordinatesArray.setVertexAttribPointer(0,
            cubeGLESProgram.aTextureCoordinates.location, 2, 0)
    }

    private fun setTexture() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(cubeGLESProgram.mUTextureLocation.location, 0)
    }

    override fun draw() {
        glDrawArrays(
            GL_TRIANGLES, 0,
            vertexArray.floatBuffer.capacity() / positionComponentCount
        )
    }
}