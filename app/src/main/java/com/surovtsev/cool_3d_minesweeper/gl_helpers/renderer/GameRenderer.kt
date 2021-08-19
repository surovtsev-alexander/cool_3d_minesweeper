package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.clik_pointer.ClickPointer
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.Cubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.CubesCoordinatesGeneratorConfig
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.GLCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.CameraInfo
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.ClickHandler
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.utils.LoggerConfig
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec3.Vec3s
import glm_.vec4.Vec4
import java.lang.StringBuilder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    var mModelObjects: ModelObjects? = null
    var mScene: Scene? = null

    inner class ModelObjects {
        val glCubes: GLCubes
        val mClickPointer: ClickPointer

        init {
            val d: Short = if (false) {
                12
            } else {
                2
            }

            val xDim = d
            val yDim = d
            val zDim = d

            val counts = Vec3s(xDim, yDim, zDim)

            val dimensions = Vec3(5f, 5f, 5f)
            val gaps = dimensions / counts / 40
            val cubesConfig = CubesCoordinatesGeneratorConfig(
                counts,
                dimensions,
                gaps
            )
            glCubes = GLCubes(
                context,
                Cubes.cubes(
                    CubesCoordinatesGenerator.generateCubesCoordinates(
                        cubesConfig
                    )
                )
            )

            mClickPointer = ClickPointer(context)
        }
    }

    inner class Scene(val modelObjects: ModelObjects, width: Int, height: Int) {
        val mCameraInfo: CameraInfo
        val mClickHandler: ClickHandler

        init {
            mCameraInfo = CameraInfo(width, height)
            mClickHandler = ClickHandler(mCameraInfo)
        }

        fun onSurfaceChanged() {
            val glObject = modelObjects.glCubes.glObject

            val mVPMatrix = mCameraInfo.mMVPMatrix
            with(glObject.mModelModelGLSLProgram) {
                use_program()
                fillU_MVP_Matrix(mVPMatrix)
            }

            with(modelObjects.mClickPointer.mGLSLProgram) {
                use_program()
                fillU_MVP_Matrix(mVPMatrix)
            }
        }

        fun onDrawFrame() {
            val moveHandler = mCameraInfo.mMoveHandler
            val moved = moveHandler.getAndRelease()
            val clicked = mClickHandler.isUpdated()

            if (moved) {
                mCameraInfo.recalculateMVPMatrix()
            }

            do {
                if (clicked) {
                    mClickHandler.release()
                    modelObjects.mClickPointer.needToBeDrawn = true
                }

                if (!modelObjects.mClickPointer.needToBeDrawn) {
                    break
                }

                if (clicked) {
                    modelObjects.mClickPointer.setPoints(mClickHandler.pointer)
                }

                modelObjects.mClickPointer.mGLSLProgram.use_program()
                if (moved) {
                    with(modelObjects.mClickPointer.mGLSLProgram) {
                        fillU_MVP_Matrix(mCameraInfo.mMVPMatrix)
                    }
                }
                modelObjects.mClickPointer.bindData()
                modelObjects.mClickPointer.draw()
            } while (false)


            val glCubes = modelObjects.glCubes
            val glObject = glCubes.glObject
            glObject.mModelModelGLSLProgram.use_program()

            glObject.bindData()
            if (clicked) {
                glCubes.testPointer(mClickHandler.pointer)
            }
            if (moved) {
                with(glObject.mModelModelGLSLProgram) {
                    fillU_MVP_Matrix(mCameraInfo.mMVPMatrix)
                }
            }
            glObject.draw()
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        mModelObjects = ModelObjects()
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
}