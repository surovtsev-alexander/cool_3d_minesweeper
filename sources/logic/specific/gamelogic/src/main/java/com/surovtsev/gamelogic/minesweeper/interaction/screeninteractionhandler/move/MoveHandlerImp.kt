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


package com.surovtsev.gamelogic.minesweeper.interaction.screeninteractionhandler.move

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.camerainfohelperholder.CameraInfoHelperHolder
import com.surovtsev.touchlistener.helpers.handlers.MoveHandler
import com.surovtsev.utils.math.MatrixHelper
import glm_.vec2.Vec2
import javax.inject.Inject

@GameScope
class MoveHandlerImp @Inject constructor(
    private val cameraInfoHelperHolder: CameraInfoHelperHolder,
):
    MoveHandler
{
    override fun rotate(prev: Vec2, curr: Vec2) {
        val cameraInfoHelper = cameraInfoHelperHolder.cameraInfoHelperFlow.value ?: return

        val farRawCalculator = { proj: Vec2 ->
            cameraInfoHelper.calcFarRawByProj(
                cameraInfoHelper.normalizedDisplayCoordinates(
                    proj
                )
            )
        }
        
        val rotation = MatrixHelper.calcRotMatrix(
            farRawCalculator(prev),
            farRawCalculator(curr)
        )

        cameraInfoHelper.multiplyRotationMatrix(rotation)
    }

    override fun scale(factor: Float) {
        cameraInfoHelperHolder.cameraInfoHelperFlow.value?.scale(factor)
    }

    override fun move(prev: Vec2, curr: Vec2) {
        val cameraInfoHelper = cameraInfoHelperHolder.cameraInfoHelperFlow.value ?: return

        val nearWorldCalculator = { proj: Vec2 ->
            cameraInfoHelper.calcNearWorldPointByProj(
                cameraInfoHelper.normalizedDisplayCoordinates(
                    proj
                )
            )
        }

        val diff = nearWorldCalculator(curr) - nearWorldCalculator(prev)

        cameraInfoHelper.translate(diff)
    }
}
