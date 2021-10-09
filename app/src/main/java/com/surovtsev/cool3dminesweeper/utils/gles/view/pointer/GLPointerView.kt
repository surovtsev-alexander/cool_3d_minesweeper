package com.surovtsev.cool3dminesweeper.utils.gles.view.pointer

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.gles.interfaces.OpenGLObject
import com.surovtsev.cool3dminesweeper.utils.gles.model.buffers.VertexArray
import com.surovtsev.cool3dminesweeper.utils.gles.model.pointer.Pointer
import com.surovtsev.cool3dminesweeper.utils.gles.model.program.PointerGLESProgram
import com.surovtsev.cool3dminesweeper.utils.statehelpers.Switch
import com.surovtsev.cool3dminesweeper.utils.statehelpers.SwitchImp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/* TODO: refactoring */

@GameScope
class GLPointerView @Inject constructor(
    @ApplicationContext private val context: Context
):
    OpenGLObject, Switch by SwitchImp()
{
    companion object {
        private const val positionComponentCount = 3
    }

    var mGLESProgram: PointerGLESProgram? = null
    private var vertexArray: VertexArray? = null

    fun onSurfaceCreated() {
        mGLESProgram = PointerGLESProgram(context)
        vertexArray = VertexArray(FloatArray(2 * 3))
        mGLESProgram!!.prepareProgram()
        glLineWidth(mGLESProgram!!.mLineWidth)

    }

    override fun bindData() {
        vertexArray!!.setVertexAttribPointer(0, mGLESProgram!!.mAPosition.location,
            positionComponentCount, 0)
    }

    fun setPoints(pointer: Pointer) {
        val near = pointer.near
        val far = pointer.far
        val x = floatArrayOf(
            near[0], near[1], near[2],
            far[0], far[1], far[2])
        vertexArray!!.updateBuffer(x, 0, x.count())
    }

    override fun draw() {
        glDrawArrays(
            GL_LINES, 0,
            vertexArray!!.floatBuffer.capacity() / positionComponentCount)
    }
}
