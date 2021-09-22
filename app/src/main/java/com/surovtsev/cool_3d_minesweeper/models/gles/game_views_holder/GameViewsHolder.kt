package com.surovtsev.cool_3d_minesweeper.models.gles.game_views_holder

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool_3d_minesweeper.views.opengl.CubeView

data class GameViewsHolder(
    val glPointerView: GLPointerView,
    val cubeView: CubeView
) {
    companion object {
        fun createObject(
            context: Context,
            cubeCoordinates: CubeCoordinates
        ): GameViewsHolder {
            val glPointerView = GLPointerView(context)
            val cubeView = CubeView(
                context, cubeCoordinates
            )

            return GameViewsHolder(
                glPointerView,
                cubeView
            )
        }
    }

    fun onSurfaceCreated() {
        glPointerView.onSurfaceCreated()
        cubeView.onSurfaceCreated()
    }
}
