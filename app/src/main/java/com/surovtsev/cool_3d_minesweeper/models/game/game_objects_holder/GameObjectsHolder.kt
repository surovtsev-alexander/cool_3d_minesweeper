package com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameTouchHandler
import com.surovtsev.cool_3d_minesweeper.models.game.GameObject
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGeneratorConfig
import com.surovtsev.cool_3d_minesweeper.models.game.cube.Cube
import com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.GLCube
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.CubeViewHelper
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.CubeCellCalculator
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

class GameObjectsHolder(
    private val context: Context,
    private val gameStatusesReceiver: IGameStatusesReceiver
) {

    val cubeCellCalculator: CubeCellCalculator
    val glPointerView: GLPointerView
    val cube: Cube
    val gameObject: GameObject
    val glCube: GLCube

    init {
        val d: Short = if (true) {
            12
        } else {
            7
        }

        val xDim = d
        val yDim = d
        val zDim = d

        val counts = Vec3s(xDim, yDim, zDim)

        val dimensions = Vec3(5f, 5f, 5f)
        val gaps = if (false) dimensions / counts / 40 else if (true) Vec3() else dimensions / counts / 10
        val bombsRate =  if (true)  {
            0.2f
        } else {
            0.1f
        }
        val cubesConfig =
            CubesCoordinatesGeneratorConfig(
                counts,
                dimensions,
                gaps,
                bombsRate,
                gameStatusesReceiver
            )

        val cubesCoordinates =
            CubesCoordinatesGenerator.generateCubesCoordinates(
                cubesConfig
            )


        cube =
            Cube(
                counts,
                cubesConfig.cellSphereRadius,
                cubesCoordinates.centers,
                cubesConfig.halfCellSpace
            )

        gameObject =
            GameObject(
                cube.counts,
                cubesConfig.bombsCount
            )

        val cubeViewHelper = CubeViewHelper.calculateCoordinates(
            cubesCoordinates
        )

        glCube =
            GLCube(
                context,
                cubeViewHelper.triangleCoordinates,
                cubeViewHelper.isEmpty,
                cubeViewHelper.textureCoordinates
            )

        val gameTouchHandler =
            GameTouchHandler(
                gameObject,
                glCube,
                gameStatusesReceiver
            )

        cubeCellCalculator = CubeCellCalculator(
            context,
            cubeViewHelper,
            gameObject,
            gameTouchHandler,
            cube
        )

        glPointerView = GLPointerView(context)
    }
}
