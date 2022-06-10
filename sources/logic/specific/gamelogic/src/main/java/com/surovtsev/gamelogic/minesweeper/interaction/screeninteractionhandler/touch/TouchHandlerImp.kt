/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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
        pointer.near = cameraInfoHelper.calcNearRawByProj(proj)
        pointer.far = cameraInfoHelper.calcFarRawByProj(proj)
        pointer.touchType = touchType

        update()
    }
}
