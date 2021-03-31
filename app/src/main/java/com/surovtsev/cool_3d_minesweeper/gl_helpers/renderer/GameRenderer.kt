package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.GLObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.MoveHandler
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.Cubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.GLCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.IndexedCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.tests.t_002_triangles.Triangles
import com.surovtsev.cool_3d_minesweeper.gl_helpers.program.GLSL_Program
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.CameraInfo
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHandler
import com.surovtsev.cool_3d_minesweeper.math.Point3d
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    private var mGLSLProgram: GLSL_Program? = null

    private var mGLObject: GLObject? = null
    val mMoveHandler = MoveHandler()
    var mCameraInfo: CameraInfo? = null
        private set
    var mClickHandler: ClickHandler? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        mGLSLProgram = GLSL_Program(context)
        mGLSLProgram!!.load_program()
        mGLSLProgram!!.use_program()
        mGLSLProgram!!.load_locations()

        if (true) {
            val cubesConfig = IndexedCubes.Companion.CubesConfig(
                Point3d(3, 3, 3),
                Point3d(5f, 5f, 5f),
                Point3d(0.02f, 0.02f, 0.02f)
            )
            mGLObject = GLCubes(
                mGLSLProgram!!,
                Cubes.cubes(
                    IndexedCubes.indexedCubes(
                        cubesConfig
                    )
                )
            ).glObject
        } else {
            mGLObject = Triangles(mGLSLProgram!!).glslObject
        }
        mGLObject!!.bind_attribs()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        mCameraInfo = CameraInfo(width, height)
        mClickHandler = ClickHandler(mCameraInfo!!)

        mGLSLProgram!!.use_program()

        mGLSLProgram!!.set_vp_matrix(mCameraInfo!!.mViewProjectionMatrix)
        mGLSLProgram!!.set_u_matrix(mMoveHandler.mMatrix)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if (mMoveHandler.mUpdated) {
            mMoveHandler.updateMatrix()
            mGLSLProgram!!.set_u_matrix(mMoveHandler.mMatrix)
        }
        mGLObject!!.draw()
    }

}