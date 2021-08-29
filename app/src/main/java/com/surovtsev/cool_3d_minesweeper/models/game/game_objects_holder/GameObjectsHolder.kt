package com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameObject
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.CubesCoordinatesGeneratorConfig
import com.surovtsev.cool_3d_minesweeper.models.game.cube.Cube
import com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.CubeViewHelper
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.objects.cubes.GLCellHelper
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

class GameObjectsHolder(
    private val context: Context,
    private val gameStatusesReceiver: IGameStatusesReceiver
) {
    val glCellHelper: GLCellHelper
    val glPointerView: GLPointerView

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


        val cube =
            Cube(
                counts,
                cubesConfig.cellSphereRadius,
                cubesCoordinates.centers,
                cubesConfig.halfCellSpace
            )

        val gameObject =
            GameObject(
                cube.counts,
                cubesConfig.bombsCount
            )

        glCellHelper = GLCellHelper(
            context,
            CubeViewHelper.calculateCoordinates(
                cubesCoordinates
            ), gameObject, gameStatusesReceiver, cube
        )

        glPointerView = GLPointerView(context)
    }
}
