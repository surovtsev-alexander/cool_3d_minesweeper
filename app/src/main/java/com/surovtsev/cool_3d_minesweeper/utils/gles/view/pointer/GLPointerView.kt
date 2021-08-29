package com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.buffers.VertexArray
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.IGLObject
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.program.PointerGLESProgram
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.IPointer
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.ISwitch
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Switch

class GLPointerView(context: Context):
    IGLObject, ISwitch by Switch() {
    private val POSITION_COMPONENT_COUNT = 3

    val mGLESProgram = PointerGLESProgram(context)
    private val vertexArray = VertexArray(FloatArray(2 * 3))

    init {
        mGLESProgram.prepareProgram()
        glLineWidth(mGLESProgram.mLineWidth)
    }

    override fun bindData() {
        vertexArray.setVertexAttribPointer(0, mGLESProgram.mAPosition.location,
            POSITION_COMPONENT_COUNT, 0)
    }

    fun setPoints(pointer: IPointer) {
        val near = pointer.near
        val far = pointer.far
        val x = floatArrayOf(
            near[0], near[1], near[2],
            far[0], far[1], far[2])
        vertexArray.updateBuffer(x, 0, x.count())
    }

    override fun draw() {
        glDrawArrays(
            GL_LINES, 0,
            vertexArray.floatBuffer.capacity() / POSITION_COMPONENT_COUNT)
    }
}
