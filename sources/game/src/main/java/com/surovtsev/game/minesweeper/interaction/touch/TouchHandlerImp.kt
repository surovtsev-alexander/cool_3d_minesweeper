package com.surovtsev.game.minesweeper.interaction.touch

import com.surovtsev.game.minesweeper.gamelogic.helpers.CameraInfoHelper
import com.surovtsev.utils.androidview.interaction.TouchType
import com.surovtsev.game.utils.gles.model.pointer.PointerImp
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.touchlistener.helpers.handlers.TouchHandler
import com.surovtsev.utils.statehelpers.UpdatableImp
import glm_.vec2.Vec2
import javax.inject.Inject

@GameScope
class TouchHandlerImp @Inject constructor(
    private val cameraInfoHelper: CameraInfoHelper,
    val pointer: PointerImp
):
    TouchHandler,
    UpdatableImp(false)
{
    override fun handleTouch(
        point: Vec2,
        touchType: TouchType
    ) {
        val proj = cameraInfoHelper.normalizedDisplayCoordinates(point)
        pointer.near = cameraInfoHelper.calcNearByProj(proj)
        pointer.far = cameraInfoHelper.calcFarByProj(proj)
        pointer.touchType = touchType

        update()
    }
}
