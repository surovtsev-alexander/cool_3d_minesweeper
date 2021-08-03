package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common

import android.opengl.GLES20.*
import com.surovtsev.cool_3d_minesweeper.gl_helpers.data.VertexArray
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.Model_GLSL_Program

class ModelObject(modelGlslProgram: Model_GLSL_Program,
                  val trianglesCoordinates: FloatArray,
                  val trianglesNums: FloatArray,
                  val trianglesTextures: FloatArray,
                  val textureCoordinates: FloatArray,
                  val textureId: Int): IGLObject(modelGlslProgram)
{
    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray = VertexArray(trianglesCoordinates)
    private val numsArray = VertexArray(trianglesNums)
    private val texturesArray = VertexArray(trianglesTextures)
    private val textureCoordinatesArray = VertexArray(textureCoordinates)

    override fun bind_attributes() {
        vertexArray.setVertexAttribPointer(0, modelGlslProgram._a_position_location,
            POSITION_COMPONENT_COUNT, 0)
        numsArray.setVertexAttribPointer(0, modelGlslProgram._a_triangle_num,
            1, 0)
        texturesArray.setVertexAttribPointer(0, modelGlslProgram._a_triangle_texture,
            1, 0)
        textureCoordinatesArray.setVertexAttribPointer(0,
            modelGlslProgram._a_texture_coordinates, 2, 0)
    }

    fun set_texture() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(modelGlslProgram._u_texture_location, 0)
    }

    override fun draw() {
        glDrawArrays(
            GL_TRIANGLES, 0,
            vertexArray.floatBuffer.capacity() / POSITION_COMPONENT_COUNT
        )
    }
}