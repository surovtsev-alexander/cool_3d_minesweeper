package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch

import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CameraInfoHelper
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.IPointer
import com.surovtsev.cool_3d_minesweeper.utils.logger_config.LoggerConfig
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

        if (LoggerConfig.LOG_CLICK_HANDLER_DATA) {
            val message = arrayOf<String>(
                "proj:$proj",
                "near:${pointer.near}",
                "far:${pointer.far}"
            ).reduce {acc, x -> "$acc\n$x"}
            ApplicationController.getInstance().messagesComponent?.addMessageUI(message)
        }
    }
}