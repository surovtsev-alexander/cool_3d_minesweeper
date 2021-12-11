package com.surovtsev.cool3dminesweeper.utils.gles.view.pointer

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool3dminesweeper.utils.gles.interfaces.OpenGLModel
import com.surovtsev.game.utils.gles.model.buffers.VertexArray
import com.surovtsev.game.utils.gles.model.pointer.Pointer
import com.surovtsev.game.utils.gles.model.program.PointerGLESProgram
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.core.utils.statehelpers.Switch
import com.surovtsev.core.utils.statehelpers.SwitchImp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/* TODO: refactoring */

@GameScope
class PointerOpenGLModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pointer: Pointer,
    val mGLESProgram: PointerGLESProgram,
):
    OpenGLModel, Switch by SwitchImp()
{
    companion object {
        private const val positionComponentCount = 3
    }

    private var vertexArray: VertexArray = VertexArray()

    fun onSurfaceCreated() {
        vertexArray.allocateBuffer(2 * 3)
        mGLESProgram.prepareProgram()
        glLineWidth(mGLESProgram.mLineWidth)

    }

    override fun bindData() {
        vertexArray.setVertexAttribPointer(0, mGLESProgram.mAPosition.location,
            positionComponentCount, 0)
    }

    fun updatePoints() {
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
            vertexArray.floatBuffer.capacity() / positionComponentCount)
    }
}
