package com.surovtsev.cool3dminesweeper.views.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool3dminesweeper.R
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.CubeViewDataHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.scene.texturecoordinateshelper.TextureCoordinatesHelper
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.cellpointers.PointedCell
import com.surovtsev.cool3dminesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool3dminesweeper.models.gles.programs.CubeGLESProgram
import com.surovtsev.cool3dminesweeper.utils.gles.helpers.TextureHelper
import com.surovtsev.cool3dminesweeper.utils.gles.interfaces.ICanUpdateTexture
import com.surovtsev.cool3dminesweeper.utils.gles.interfaces.IGLObject
import com.surovtsev.cool3dminesweeper.utils.gles.model.buffers.VertexArray
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@GameScope
class CubeView @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cubeCoordinates: CubeCoordinates
):
    IGLObject,
    ICanUpdateTexture
{
    var cubeGLESProgram: CubeGLESProgram? = null

    private var textureId: Int = -1

    private var vertexArray: VertexArray? = null
    private var isEmptyArray: VertexArray? = null
    private var textureCoordinatesArray: VertexArray? = null

    fun onSurfaceCreated() {
        cubeGLESProgram = CubeGLESProgram(context)
        textureId = TextureHelper.loadTexture(context, R.drawable.skin)

        val cubeViewDataHelper = CubeViewDataHelper.createObject(
            cubeCoordinates
        )

        vertexArray = VertexArray(cubeViewDataHelper.triangleCoordinates)
        isEmptyArray = VertexArray(cubeViewDataHelper.isEmpty)
        textureCoordinatesArray = VertexArray(cubeViewDataHelper.textureCoordinates)


        cubeGLESProgram!!.prepareProgram()
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

            isEmptyArray!!.updateBuffer(
                onesEmpty,
                startPos
            )
        } else {
            val startPos = textureIndexesCount * id

            val resArray = TextureCoordinatesHelper.getTextureCoordinates(skin.texture)
            textureCoordinatesArray!!.updateBuffer(
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
        cubeSkin.iterateCubes { xyz ->
            val skin = xyz.getValue(skins)
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

        isEmptyArray!!.updateBuffer(
            emptyCubes, 0
        )
        textureCoordinatesArray!!.updateBuffer(
            textureCoordinates, 0
        )
    }

    override fun bindData() {
        vertexArray!!.setVertexAttribPointer(0, cubeGLESProgram!!.aPosition.location,
            positionComponentCount, 0)
        isEmptyArray!!.setVertexAttribPointer(0, cubeGLESProgram!!.aIsEmpty.location,
            1, 0)
        textureCoordinatesArray!!.setVertexAttribPointer(0,
            cubeGLESProgram!!.aTextureCoordinates.location, 2, 0)
    }

    private fun setTexture() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(cubeGLESProgram!!.mUTextureLocation.location, 0)
    }

    override fun draw() {
        glDrawArrays(
            GL_TRIANGLES, 0,
            vertexArray!!.floatBuffer.capacity() / positionComponentCount
        )
    }
}