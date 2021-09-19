package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.KeyEvent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.dagger.AppComponent
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.receiver.TouchListenerReceiver
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroyKeyDown
import org.jetbrains.anko.runOnUiThread

class GameActivityModelView(
    val context: Context,
    private val appComponent: AppComponent
):
    IGameEventsReceiver,
    IHandlePauseResumeDestroyKeyDown
{

    val marking = MyLiveData(false)
    val elapsedTime = MyLiveData(0L)
    val bombsLeft = MyLiveData(0)
    val showDialog = MyLiveData(false)


    val minesweeperController = MinesweeperController(
        context,
        this,
        appComponent
    )
    private val gameControls = minesweeperController.gameControls
    private val removeBombs = gameControls.removeBombs
    private val removeZeroBorders = gameControls.removeZeroBorders
    private val markOnShortTap = gameControls.markOnShortTap

    /* TODO: move to activity class */
    var glSurfaceView: GLSurfaceView? = null

    init {
        (this as IGameEventsReceiver).init()
    }

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
        context.runOnUiThread {
            if (GameStatusHelper.isGameOver(newStatus)) {
                showDialog.onDataChanged(true)
            }
        }
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

    override fun onPause() {
        minesweeperController.onPause()
        glSurfaceView?.onPause()
    }

    override fun onResume() {
        minesweeperController.onResume()
        glSurfaceView?.onResume()
    }

    override fun onDestroy() {
        minesweeperController.onDestroy()
    }

    override fun onKeyDown(keyCode: Int): Boolean {
        if (
            keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        ) {
            setMarking(
                !(marking.data.value!!)
            )

            return true
        }

        return false
    }
}