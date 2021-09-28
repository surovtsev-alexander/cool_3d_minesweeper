package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CameraInfoHelper
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.Pointer
import glm_.vec2.Vec2
import javax.inject.Inject

@GameControllerScope
class TouchHandler @Inject constructor(
    private val cameraInfoHelper: CameraInfoHelper,
    val pointer: Pointer
):
    Updatable(false)
{
    fun handleTouch(point: Vec2, touchType: TouchType) {
        val proj = cameraInfoHelper.normalizedDisplayCoordinates(point)
        pointer.near = cameraInfoHelper.calcNearByProj(proj)
        pointer.far = cameraInfoHelper.calcFarByProj(proj)
        pointer.touchType = touchType

        update()
    }
}