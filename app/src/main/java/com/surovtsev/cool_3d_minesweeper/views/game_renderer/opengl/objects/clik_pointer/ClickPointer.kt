package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.clik_pointer

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.utils.opengl.buffers.VertexArray
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.common.IGLObject
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.programs.CLickPointerGLSLProgram
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.IPointer

class ClickPointer(context: Context): IGLObject {
    private val POSITION_COMPONENT_COUNT = 3

    val mGLSLProgram: CLickPointerGLSLProgram
    private val vertexArray = VertexArray(FloatArray(2 * 3))

    var needToBeDrawn = false

    init {
        mGLSLProgram = CLickPointerGLSLProgram(context)
        mGLSLProgram.prepare_program()
        glLineWidth(mGLSLProgram.mLineWidth)
    }

    override fun bindData() {
        vertexArray.setVertexAttribPointer(0, mGLSLProgram.mAPosition.location,
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
