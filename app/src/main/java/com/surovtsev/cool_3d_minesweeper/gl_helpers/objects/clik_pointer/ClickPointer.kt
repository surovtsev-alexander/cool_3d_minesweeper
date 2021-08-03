package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.clik_pointer

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.IGLObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.CLickPointerGLSLProgram

class ClickPointer(context: Context): IGLObject {
    private val POSITION_COMPONENT_COUNT = 3

    val mGLSLProgram: CLickPointerGLSLProgram
    private val vertexArray = VertexArray(FloatArray(2 * 3))

    var need_to_be_drawn = false

    init {
        mGLSLProgram = CLickPointerGLSLProgram(context)
        mGLSLProgram.prepare_program()
    }

    override fun bind_data() {
        vertexArray.setVertexAttribPointer(0, mGLSLProgram.mAPosition.location,
            POSITION_COMPONENT_COUNT, 0)
    }

    override fun draw() {
        glDrawArrays(
            GL_LINES, 0,
            vertexArray.floatBuffer.capacity() / POSITION_COMPONENT_COUNT)
    }
}
