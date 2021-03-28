package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.surovtsev.cool_3d_minesweeper.activities.TouchHandler
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.GLIndexedCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.IndexedCubes
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program
import com.surovtsev.cool_3d_minesweeper.math.Point3d
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    val _touchHandler = TouchHandler()

    private var _glsl_program: GLSL_Program? = null
    private var _cubes: GLIndexedCubes? = null

    val _projectionMatrix = MatrixHelper.matrix_creator()
    val _viewMatrix = MatrixHelper.matrix_creator()
    val _viewProjectionMatrix = MatrixHelper.matrix_creator()


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)

        _glsl_program = GLSL_Program(context)
        _glsl_program!!.load_program()
        _glsl_program!!.use_program()
        _glsl_program!!.load_locations()

        _cubes = GLIndexedCubes(_glsl_program!!,
            IndexedCubes.indexedCubes(
              Point3d(5, 5, 5)
            , Point3d(3f, 3f, 3f)
            , Point3d(0.02f, 0.02f, 0.02f)))
        _cubes!!.indexed_object.bind_attribs()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        if (false) {
            MatrixHelper.perspectiveM(
                _projectionMatrix,
                45f,
                width.toFloat() / height.toFloat(),
                1f, 10f
            )
        } else {
            Matrix.perspectiveM(
                _projectionMatrix,
                0, 30f,
                width.toFloat() / height.toFloat(), 2f, 20f
            )
        }
        Matrix.setIdentityM(_viewMatrix, 0)
        Matrix.translateM(_viewMatrix, 0, 0f, 0f, -10f)
        Matrix.multiplyMM(_viewProjectionMatrix, 0,
            _projectionMatrix, 0,
            _viewMatrix, 0)

        _glsl_program!!.use_program()

        _glsl_program!!.set_vp_matrix(_viewProjectionMatrix)
        _glsl_program!!.set_u_matrix(_touchHandler._matrix)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if (_touchHandler._updated) {
            _touchHandler.updateMatrix()
            _glsl_program!!.set_u_matrix(_touchHandler._matrix)
        }
        _cubes!!.indexed_object.draw()
    }

}