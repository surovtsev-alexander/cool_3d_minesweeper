package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.clik_pointer.ClickPointer
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.Cubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.CubesCoordinatesGeneratorConfig
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.GLCubes
import com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.*
import glm_.vec3.Vec3
import glm_.vec3.Vec3s
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(val context: Context): GLSurfaceView.Renderer {

    var modelObjects: ModelObjects? = null
    var scene: Scene? = null

    val rendereTimer = RendererTimer()
    val clickHelper = ClickHelper(rendereTimer)

    inner class ModelObjects {
        val glCubes: GLCubes
        val clickPointer: ClickPointer

        init {
            val d: Short = if (true) {
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

            clickPointer = ClickPointer(context)
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

            val mVPMatrix = mCameraInfo.MVP
            with(glObject.modelModelGLSLProgram) {
                use_program()
                fillU_MVP(mVPMatrix)
            }

            with(modelObjects.clickPointer.mGLSLProgram) {
                use_program()
                fillU_MVP(mVPMatrix)
            }
        }

        fun onDrawFrame() {
            val moveHandler = mCameraInfo.moveHandler
            val moved = moveHandler.getAndRelease()
            val clicked = mClickHandler.isUpdated()

            if (moved) {
                mCameraInfo.recalculateMVPMatrix()
            }

            do {
                if (clicked) {
                    mClickHandler.release()
                    modelObjects.clickPointer.needToBeDrawn = true
                }

                if (!modelObjects.clickPointer.needToBeDrawn) {
                    break
                }

                if (clicked) {
                    modelObjects.clickPointer.setPoints(mClickHandler.pointer)
                }

                modelObjects.clickPointer.mGLSLProgram.use_program()
                if (moved) {
                    with(modelObjects.clickPointer.mGLSLProgram) {
                        fillU_MVP(mCameraInfo.MVP)
                    }
                }
                modelObjects.clickPointer.bindData()
                modelObjects.clickPointer.draw()
            } while (false)


            val glCubes = modelObjects.glCubes
            val glObject = glCubes.glObject
            glObject.modelModelGLSLProgram.use_program()

            glObject.bindData()
            if (clicked) {
                glCubes.testPointer(mClickHandler.pointer, clickHelper.clickType, rendereTimer.time)
            }
            if (moved) {
                with(glObject.modelModelGLSLProgram) {
                    fillU_MVP(mCameraInfo.MVP)
                }
            }
            glObject.draw()
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        modelObjects = ModelObjects()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        scene = Scene(modelObjects!!, width, height)

        scene!!.onSurfaceChanged()
    }

    override fun onDrawFrame(gl: GL10?) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable (GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        rendereTimer.updateTimer()
        clickHelper.tick()

        if (clickHelper.isClicked()) {

            scene?.mClickHandler?.handleClick(clickHelper.clickPos, clickHelper.clickType)
            clickHelper.release()
        }

        scene?.onDrawFrame()
    }
}