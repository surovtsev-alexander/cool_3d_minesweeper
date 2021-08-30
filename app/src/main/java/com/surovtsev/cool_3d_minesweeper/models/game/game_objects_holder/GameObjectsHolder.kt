package com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubesCoordinatesGenerator
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.border.cube.CubeBorder
import com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool_3d_minesweeper.views.opengl.CubeView
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.CubeViewFactory

class GameObjectsHolder(
    context: Context,
    gameConfig: GameConfig
) {
    val cubeBorder: CubeBorder
    val cubeSkin: CubeSkin
    val cubeViewHelper: CubeViewFactory
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

        cubeViewHelper = CubeViewFactory.getCubeView(
            cubesCoordinates
        )

    }
}
