package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.clik_pointer.ClickPointer
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.common.ModelObject
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.MoveHandler
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.Cubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.GLCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.IndexedCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.tests.t_002_triangles.Triangles
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.CameraInfo
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHandler
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.MatrixHelper
import com.surovtsev.cool_3d_minesweeper.math.Point3d
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import java.lang.StringBuilder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    private var mModelObject: ModelObject? = null
    val mMoveHandler = MoveHandler()
    var mCameraInfo: CameraInfo? = null
        private set
    var mClickHandler: ClickHandler? = null

    private var mClickPointer: ClickPointer? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        fun load_model() {
            if (true) {
                val counts = if (true) {
                    Point3d<Short>(3, 3, 3)
                } else {
                    Point3d<Short>(1, 1, 1)
                }
                val cubesConfig = IndexedCubes.Companion.CubesConfig(
                    counts,
                    Point3d(5f, 5f, 5f),
                    Point3d(0.02f, 0.02f, 0.02f)
                )
                    mModelObject = GLCubes(
                    context,
                    Cubes.cubes(
                        IndexedCubes.indexedCubes(
                            cubesConfig))).glObject
            } else {
                mModelObject = Triangles(context).glslObject
            }
        }

        fun load_click_pointer() {
            mClickPointer = ClickPointer(context)
        }

        load_model()
        load_click_pointer()

        if (LoggerConfig.LOG_SCENE) {
            ApplicationController.instance!!.logScene = this::logScene
        }
    }

    fun logScene() {
        val vp_matrix = mCameraInfo!!.mViewProjectionMatrix
        val model_matrix = mMoveHandler.rotMatrix
        val mvp_matrix = vp_matrix * model_matrix
        val points = mModelObject!!.trianglesCoordinates

        val test_str = StringBuilder()

        val matrix_to_str = {
                caption: String, matrix: Mat4 ->
            "$caption\n${matrix}"
        }

        if (true) {
            test_str.append("width: ${mCameraInfo!!.mDisplayWidthF}\n")
            test_str.append("height: ${mCameraInfo!!.mDisplayHeightF}\n")
        }

        if (true) {
            test_str.append(
                matrix_to_str("projection_matrix", mCameraInfo!!.mProjectionMatrix)
            )
            test_str.append("\n")
        }

        if (true) {
            test_str.append(
                matrix_to_str("vp_matrix", vp_matrix)
            )
            test_str.append("\n")
        }
        if (true) {
            test_str.append(
                matrix_to_str("model_matrix", model_matrix)
            )
            test_str.append("\n")
        }
        if (true) {
            test_str.append(
                matrix_to_str("mvp_matrix", mvp_matrix)
            )
            test_str.append("\n")
        }

        if (true) {
            test_str.append("points:\n")
            for (i in 0 until points.count() / 3) {
                val p = Vec4(points[i * 3], points[i * 3 + 1], points[i * 3 + 2], 1f)
                val pp = mvp_matrix * p
                val ppn = Vec3(pp) / pp[3]

                test_str.append("$p -> $pp -> $ppn\n")
            }
        }

        Log.d("TEST", test_str.toString())
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        mCameraInfo = CameraInfo(width, height, moveHandler = mMoveHandler)
        mClickHandler = ClickHandler(mCameraInfo!!)

        mModelObject!!.mModelModelGLSLProgram.use_program()

        mModelObject!!.mModelModelGLSLProgram.fillU_VP_Matrix(mCameraInfo!!.mViewProjectionMatrix)
        mModelObject!!.mModelModelGLSLProgram.fillU_M_Matrix(MatrixHelper.identity_matrix())

        mClickPointer!!.mGLSLProgram.use_program()
        mClickPointer!!.mGLSLProgram.fillU_VP_Matrix(mCameraInfo!!.mViewProjectionMatrix)
    }

    override fun onDrawFrame(gl: GL10?) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable (GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        mModelObject!!.mModelModelGLSLProgram.use_program()
        mModelObject!!.bind_data()
        if (mMoveHandler.mUpdated) {
            mCameraInfo!!.calculate_matrices()
            //mModelObject!!.mModelModelGLSLProgram.fillU_M_Matrix(mMoveHandler.rotMatrix)
            mModelObject!!.mModelModelGLSLProgram!!.fillU_VP_Matrix(mCameraInfo!!.mViewProjectionMatrix)
        }
        mModelObject!!.draw()

        do {
            val updated = mClickHandler!!.isUpdated()

            if (updated) {
                mClickHandler!!.release()
                mClickPointer!!.need_to_be_drawn = true
            }

            if (!mClickPointer!!.need_to_be_drawn) {
                break
            }

            if (updated) {
                mClickPointer!!.set_points(mClickHandler!!.x_near, mClickHandler!!.x_far)
            }

            mClickPointer!!.mGLSLProgram.use_program()
            if (mMoveHandler.mUpdated) {
                mClickPointer!!.mGLSLProgram.fillU_VP_Matrix(mCameraInfo!!.mViewProjectionMatrix)
            }
            mClickPointer!!.bind_data()
            mClickPointer!!.draw()
        } while (false)

        if (mMoveHandler.mUpdated) {
            mMoveHandler.updateMatrix()
        }
    }

}