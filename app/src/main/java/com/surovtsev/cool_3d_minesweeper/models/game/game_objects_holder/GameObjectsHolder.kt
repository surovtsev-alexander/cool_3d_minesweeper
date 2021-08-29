package com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.GameTouchHandler
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.helpers.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.border.cube.CubeBorder
import com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool_3d_minesweeper.views.opengl.CubeView
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.CubeViewFactory

class GameObjectsHolder(
    private val context: Context,
    private val gameStatusesReceiver: IGameStatusesReceiver,
    private val gameConfig: GameConfig
) {
    val glPointerView: GLPointerView
    val cubeBorder: CubeBorder
    val cubeSkin: CubeSkin
    val cubeView: CubeView
    val gameTouchHandler: GameTouchHandler

    init {

        val cubesCoordinates =
            CubesCoordinatesGenerator.generateCubesCoordinates(
                gameConfig
            )


        cubeBorder =
            CubeBorder(
                gameConfig,
                cubesCoordinates.centers
            )

        cubeSkin =
            CubeSkin(
                gameConfig
            )

        val cubeViewHelper = CubeViewFactory.getCubeView(
            cubesCoordinates
        )

        cubeView =
            CubeView(
                context,
                cubeViewHelper.triangleCoordinates,
                cubeViewHelper.isEmpty,
                cubeViewHelper.textureCoordinates
            )

        gameTouchHandler =
            GameTouchHandler(
                cubeSkin,
                cubeView,
                gameStatusesReceiver,
                gameConfig
            )

        glPointerView = GLPointerView(context)
    }
}
