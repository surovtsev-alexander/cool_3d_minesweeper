package com.surovtsev.gamescreen.views.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.gamescreen.R
import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.gamescreen.minesweeper.helpers.CubeViewDataHelper
import com.surovtsev.gamescreen.minesweeper.scene.texturecoordinateshelper.TextureCoordinatesHelper
import com.surovtsev.gamescreen.models.game.cellpointers.PointedCell
import com.surovtsev.gamescreen.models.game.skin.cube.CubeSkin
import com.surovtsev.gamescreen.models.gles.programs.CubeGLESProgram
import com.surovtsev.gamescreen.utils.gles.model.buffers.VertexArray
import com.surovtsev.gamescreen.utils.utils.gles.OpenGLModel
import com.surovtsev.gamescreen.utils.utils.gles.TextureUpdater
import com.surovtsev.utils.gles.helpers.TextureHelper
import javax.inject.Inject

@GameScope
class CubeOpenGLModel @Inject constructor(
    private val context: Context,
    private val cubeCoordinates: CubeCoordinates,
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
        textureId = TextureHelper.loadTexture(context, R.drawable.skin)

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

    fun updateTexture(cubeSkin: CubeSkin) {
        val cubesCount = cubeSkin.cellCount

        val emptyCubes =
            FloatArray(cubeIndexesCount * cubesCount) { 0f }
        val textureCoordinates =
            FloatArray(textureIndexesCount * cubesCount)

        val skins = cubeSkin.skins
        cubeSkin.iterateCubes { cellIndex ->
            val skin = cellIndex.getValue(skins)
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