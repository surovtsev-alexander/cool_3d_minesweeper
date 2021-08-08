package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.clik_pointer

import android.content.Context
import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.IGLObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.CLickPointerGLSLProgram
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.IPointer
import glm_.set
import glm_.vec3.Vec3

class ClickPointer(context: Context): IGLObject {
    private val POSITION_COMPONENT_COUNT = 3

    val mGLSLProgram: CLickPointerGLSLProgram
    private val vertexArray = VertexArray(FloatArray(2 * 3))

    var need_to_be_drawn = false

    init {
        mGLSLProgram = CLickPointerGLSLProgram(context)
        mGLSLProgram.prepare_program()
        glLineWidth(mGLSLProgram.mLineWidth)
    }

    override fun bind_data() {
        vertexArray.setVertexAttribPointer(0, mGLSLProgram.mAPosition.location,
            POSITION_COMPONENT_COUNT, 0)
    }

    fun set_points(pointer: IPointer) {
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
