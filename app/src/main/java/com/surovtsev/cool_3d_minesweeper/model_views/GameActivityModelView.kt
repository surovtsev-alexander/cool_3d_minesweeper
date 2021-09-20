package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.KeyEvent
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.receiver.TouchListenerReceiver
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroyKeyDown
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.UpdatableOnOffSwitch
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject

class GameActivityModelView(
    private val context: Context,
    load: Boolean
):
    IGameEventsReceiver,
    IHandlePauseResumeDestroyKeyDown
{
    val marking = MyLiveData(false)
    val elapsedTime = MyLiveData(0L)
    val bombsLeft = MyLiveData(0)
    val showDialog = MyLiveData(false)

    @Inject
    lateinit var minesweeperController: MinesweeperController

    private val gameControls: GameControls
    private val removeBombs: Updatable
    private val removeZeroBorders: Updatable
    private val markOnShortTap: UpdatableOnOffSwitch

    init {
        context.daggerComponentsHolder.createAndGetGameControllerComponent(this)
            .inject(this)

        gameControls = minesweeperController.gameControls
        removeBombs = gameControls.removeBombs
        removeZeroBorders = gameControls.removeZeroBorders
        markOnShortTap = gameControls.markOnShortTap

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
    }

    override fun onResume() {
        minesweeperController.onResume()
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