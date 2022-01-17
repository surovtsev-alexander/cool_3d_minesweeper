package com.surovtsev.gamelogic.minesweeper.interaction.screeninteractionhandler.touch

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.camerainfohelperholder.CameraInfoHelperHolder
import com.surovtsev.core.models.gles.pointer.PointerImp
import com.surovtsev.touchlistener.helpers.handlers.TouchHandler
import com.surovtsev.utils.androidview.interaction.TouchType
import com.surovtsev.utils.statehelpers.UpdatableImp
import glm_.vec2.Vec2
import javax.inject.Inject

@GameScope
class TouchHandlerImp @Inject constructor(
    private val cameraInfoHelperHolder: CameraInfoHelperHolder,
    val pointer: PointerImp
):
    TouchHandler,
    UpdatableImp(false)
{
    override fun handleTouch(
        point: Vec2,
        touchType: TouchType
    ) {
        val cameraInfoHelper = cameraInfoHelperHolder.cameraInfoHelperFlow.value ?: return

        val proj = cameraInfoHelper.normalizedDisplayCoordinates(point)
        pointer.near = cameraInfoHelper.calcNearByProj(proj)
        pointer.far = cameraInfoHelper.calcFarByProj(proj)
        pointer.touchType = touchType

        update()
    }
}
