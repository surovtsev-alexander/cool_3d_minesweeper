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
import com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers.*
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.math.RotationMatrixDecomposer
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
            val d: Short = if (false) {
                12
            } else {
                5
            }

            val xDim = d
            val yDim = d
            val zDim = d

            val counts = Vec3s(xDim, yDim, zDim)

            val dimensions = Vec3(5f, 5f, 5f)
            val gaps = if (false) dimensions / counts / 40 else Vec3()
            val bombsRate = 0.2f
            val cubesConfig = CubesCoordinatesGeneratorConfig(
                counts,
                dimensions,
                gaps,
                bombsRate
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
        val cameraInfo: CameraInfo
        val clickHandler: ClickHandler

        init {
            cameraInfo = CameraInfo(width, height)
            clickHandler = ClickHandler(cameraInfo)
        }

        fun onSurfaceChanged() {
            val glObject = modelObjects.glCubes.glObject

            val mVPMatrix = cameraInfo.MVP
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
            val moveHandler = cameraInfo.moveHandler
            val moved = moveHandler.getAndRelease()
            val clicked = clickHandler.isUpdated()

            if (moved) {
                cameraInfo.recalculateMVPMatrix()
            }

            do {
                if (clicked) {
                    clickHandler.release()
                    modelObjects.clickPointer.needToBeDrawn = true
                }

                if (!modelObjects.clickPointer.needToBeDrawn) {
                    break
                }

                if (clicked) {
                    modelObjects.clickPointer.setPoints(clickHandler.pointer)
                }

                modelObjects.clickPointer.mGLSLProgram.use_program()
                if (moved) {
                    with(modelObjects.clickPointer.mGLSLProgram) {
                        fillU_MVP(cameraInfo.MVP)
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
                glCubes.testPointer(clickHandler.pointer, clickHelper.clickType, rendereTimer.time)
            }
            if (moved) {
                with(glObject.modelModelGLSLProgram) {
                    fillU_MVP(cameraInfo.MVP)
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

            scene?.clickHandler?.handleClick(clickHelper.clickPos, clickHelper.clickType)
            clickHelper.release()

            if (false) {
                val x = RotationMatrixDecomposer.getAngles(
                    scene!!.cameraInfo!!.moveHandler.rotMatrix
                )
                ApplicationController.instance!!.messagesComponent!!.addMessageUI("$x")
            }
        }

        scene?.onDrawFrame()
    }
}