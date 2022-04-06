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
