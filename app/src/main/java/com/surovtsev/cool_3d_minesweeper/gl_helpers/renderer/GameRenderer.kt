package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.surovtsev.cool_3d_minesweeper.view.activities.TouchHandler
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.Cubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.GLCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.IndexedCubes
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.CameraInfoHandler
import com.surovtsev.cool_3d_minesweeper.math.Point3d
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    private var _glsl_program: GLSL_Program? = null
    /*
    private var _cubes: GLIndexedCubes? = null
     */
    private var _cubes: GLCubes? = null
    var mCameraInfoHandler: CameraInfoHandler? = null
        private set

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)

        _glsl_program = GLSL_Program(context)
        _glsl_program!!.load_program()
        _glsl_program!!.use_program()
        _glsl_program!!.load_locations()

        _cubes = GLCubes(_glsl_program!!,
            Cubes.cubes(
                IndexedCubes.indexedCubes(
                    Point3d(1, 1, 1)
                    , Point3d(3f, 3f, 3f)
                    , Point3d(0.02f, 0.02f, 0.02f))))
        _cubes!!.glObject.bind_attribs()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        mCameraInfoHandler = CameraInfoHandler(width, height)


        _glsl_program!!.use_program()

        _glsl_program!!.set_vp_matrix(mCameraInfoHandler!!.mViewProjectionMatrix)
        _glsl_program!!.set_u_matrix(mCameraInfoHandler!!.mTouchHandler.mMatrix)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val touchHandler = mCameraInfoHandler!!.mTouchHandler
        if (touchHandler.mUpdated) {
            touchHandler.updateMatrix()
            _glsl_program!!.set_u_matrix(touchHandler.mMatrix)
        }
        _cubes!!.glObject.draw()
    }

}