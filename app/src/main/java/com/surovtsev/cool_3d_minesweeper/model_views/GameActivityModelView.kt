package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.receiver.TouchListenerReceiver
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import org.jetbrains.anko.runOnUiThread

class GameActivityModelView(
    val context: Context
): IGameEventsReceiver {

    val marking = MyLiveData(false)
    val elapsedTime = MyLiveData(0L)
    val bombsLeft = MyLiveData(0)


    val minesweeperController = MinesweeperController(
        context,
        this,
        false)
    private val gameControls = minesweeperController.gameControls
    private val removeBombs = gameControls.removeBombs
    private val removeZeroBorders = gameControls.removeZeroBorders
    private val markOnShortTap = gameControls.markOnShortTap

    fun setMarking(newValue: Boolean) {
        marking.onDataChanged(newValue)
        if (newValue) {
            markOnShortTap.turnOn()
        } else {
            markOnShortTap.turnOff()
        }
    }

    fun removeMarkedBombs() {
        removeBombs.update()
    }

    fun removeZeroBorders() {
        removeZeroBorders.update()
    }

    override fun bombCountUpdated() {
        context.runOnUiThread {
            bombsLeft.onDataChanged(
                minesweeperController.gameLogic.bombsLeft
            )
        }
    }

    override fun timeUpdated() {
        context.runOnUiThread {
            elapsedTime.onDataChanged(
                minesweeperController.gameLogic.gameLogicStateHelper.getElapsed()
            )
        }
    }

    override fun gameStatusUpdated(newStatus: GameStatus) {

    }

    fun assignTouchListenerToGLSurfaceView(
        glSurfaceView: GLSurfaceView
    ) {
        val touchReceiverCalculator = object: ITouchReceiverCalculator {
            override fun getReceiver(): ITouchReceiver = minesweeperController.touchReceiver
        }
        val rotationReceiverCalculator = object: IRotationReceiverCalculator {
            override fun getReceiver(): IRotationReceiver? = minesweeperController.scene?.moveHandler
        }
        val scaleReceiverCalculator = object: IScaleReceiverCalculator {
            override fun getReceiver(): IScaleReceiver? = minesweeperController.scene?.moveHandler
        }
        val moveReceiverCalculator = object: IMoveReceiverCalculator {
            override fun getReceiver(): IMoveReceiver? = minesweeperController.scene?.moveHandler
        }
        val touchListenerReceiver = TouchListenerReceiver(
            glSurfaceView,
            touchReceiverCalculator,
            rotationReceiverCalculator,
            scaleReceiverCalculator,
            moveReceiverCalculator,
        )
        val touchListener = TouchListener(touchListenerReceiver)
        glSurfaceView.setOnTouchListener(touchListener)
    }
}