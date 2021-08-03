package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer

import android.content.Context
import android.opengl.GLES20.*
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

    var mModelObjects: ModelObjects? = null
    var mScene: Scene? = null

    inner class ModelObjects {
        val mGameObject: ModelObject
        val mClickPointer: ClickPointer

        init {
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
                mGameObject = GLCubes(
                    context,
                    Cubes.cubes(
                        IndexedCubes.indexedCubes(
                            cubesConfig
                        )
                    )
                ).glObject
            } else {
                mGameObject = Triangles(context).glslObject
            }

            mClickPointer = ClickPointer(context)
        }
    }

    inner class Scene(val modelObjects: ModelObjects, width: Int, height: Int) {
        val mMoveHandler = MoveHandler()
        val mCameraInfo: CameraInfo
        val mClickHandler: ClickHandler

        init {
            mCameraInfo = CameraInfo(width, height, moveHandler = mMoveHandler)
            mClickHandler = ClickHandler(mCameraInfo)
        }

        fun onSurfaceChanged() {
            modelObjects.mGameObject.mModelModelGLSLProgram.use_program()

            modelObjects.mGameObject.mModelModelGLSLProgram.fillU_VP_Matrix(mCameraInfo.mViewProjectionMatrix)
            modelObjects.mGameObject.mModelModelGLSLProgram.fillU_M_Matrix(MatrixHelper.identity_matrix())

            modelObjects.mClickPointer.mGLSLProgram.use_program()
            modelObjects.mClickPointer.mGLSLProgram.fillU_VP_Matrix(mCameraInfo.mViewProjectionMatrix)
        }

        fun onDrawFrame() {
            modelObjects.mGameObject.mModelModelGLSLProgram.use_program()
            modelObjects.mGameObject.bind_data()
            if (mMoveHandler.mUpdated) {
                mCameraInfo.recalculateViewMatrix()
                //mModelObject!!.mModelModelGLSLProgram.fillU_M_Matrix(mMoveHandler.rotMatrix)
                modelObjects.mGameObject.mModelModelGLSLProgram.fillU_VP_Matrix(mCameraInfo.mViewProjectionMatrix)
            }
            modelObjects.mGameObject.draw()

            do {
                val updated = mClickHandler.isUpdated()

                if (updated) {
                    mClickHandler.release()
                    modelObjects.mClickPointer.need_to_be_drawn = true
                }

                if (!modelObjects.mClickPointer.need_to_be_drawn) {
                    break
                }

                if (updated) {
                    val n = mClickHandler.near
                    val f = mClickHandler.far
                    modelObjects.mClickPointer.set_points(n, f)
                }

                modelObjects.mClickPointer.mGLSLProgram.use_program()
                if (mMoveHandler.mUpdated) {
                    modelObjects.mClickPointer.mGLSLProgram.fillU_VP_Matrix(mCameraInfo.mViewProjectionMatrix)
                }
                modelObjects.mClickPointer.bind_data()
                modelObjects.mClickPointer.draw()
            } while (false)

            if (mMoveHandler.mUpdated) {
                mMoveHandler.updateMatrix()
            }
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        mModelObjects = ModelObjects()

        if (LoggerConfig.LOG_SCENE) {
            ApplicationController.instance!!.logScene = this::logScene
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        mScene = Scene(mModelObjects!!, width, height)

        mScene!!.onSurfaceChanged()
    }

    override fun onDrawFrame(gl: GL10?) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable (GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        mScene?.onDrawFrame()
    }

    fun logScene() {
        val cameraInfo = mScene!!.mCameraInfo
        val vp_matrix = cameraInfo.mViewProjectionMatrix
        val model_matrix = mScene!!.mMoveHandler.rotMatrix
        val mvp_matrix = vp_matrix * model_matrix
        val points = mModelObjects!!.mGameObject.trianglesCoordinates

        val test_str = StringBuilder()

        val matrix_to_str = {
                caption: String, matrix: Mat4 ->
            "$caption\n${matrix}"
        }

        if (true) {
            test_str.append("width: ${cameraInfo.mDisplayWidthF}\n")
            test_str.append("height: ${cameraInfo.mDisplayHeightF}\n")
        }

        if (true) {
            test_str.append(
                matrix_to_str("projection_matrix", cameraInfo.mProjectionMatrix)
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
}