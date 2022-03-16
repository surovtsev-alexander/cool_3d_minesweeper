package com.surovtsev.gamelogic.utils.utils.gles.view.pointer

import android.opengl.GLES20.*
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.utils.gles.model.buffers.VertexArray
import com.surovtsev.core.models.gles.pointer.Pointer
import com.surovtsev.gamelogic.utils.gles.model.program.PointerGLESProgram
import com.surovtsev.gamelogic.utils.utils.gles.OpenGLModel
import com.surovtsev.utils.statehelpers.OnOffSwitch
import com.surovtsev.utils.statehelpers.OnOffSwitchImp
import javax.inject.Inject
import javax.inject.Named

/* TODO: refactoring */

@GameScope
class PointerOpenGLModel @Inject constructor(
    private val pointer: Pointer,
    val mGLESProgram: PointerGLESProgram,
    @Named(PointerEnabledName)
    private val pointerEnabled: Boolean,
):
    OpenGLModel(mGLESProgram), OnOffSwitch by OnOffSwitchImp()
{
    companion object {
        const val PointerEnabledName = "pointerEnabled"

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
            vertexArray.floatBuffer.capacity() / positionComponentCount
        )
    }

    override fun drawModel() {
        if (pointerEnabled) {
            super.drawModel()
        }
    }
}
