package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.KeyEvent
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.GameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.GameViewEventsNames
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.*
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.receiver.TouchListenerReceiver
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroyKeyDown
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.UpdatableOnOffSwitch
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import javax.inject.Inject
import javax.inject.Named

typealias RemoveMarkedBombsAction = () -> Unit
typealias RemoveZeroBordersAction = () -> Unit
typealias SetMarkingAction = (newValue: Boolean) -> Unit

class GameActivityModelView(
    private val context: Context
):
    IHandlePauseResumeDestroyKeyDown
{
    @Inject
    @Named(GameViewEventsNames.Marking)
    lateinit var marking: MyLiveData<Boolean>

    @Inject
    lateinit var gameEventsReceiver: GameEventsReceiver

    @Inject
    lateinit var minesweeperController: MinesweeperController
    @Inject
    lateinit var gameRenderer: GLESRenderer

    private val gameControls: GameControls
    private val removeBombs: Updatable
    private val removeZeroBorders: Updatable
    private val markOnShortTap: UpdatableOnOffSwitch

    init {
        context.daggerComponentsHolder.createAndGetGameControllerComponent()
            .inject(this)

        gameControls = minesweeperController.gameControls
        removeBombs = gameControls.removeBombs
        removeZeroBorders = gameControls.removeZeroBorders
        markOnShortTap = gameControls.markOnShortTap
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

    private fun assignTouchListenerToGLSurfaceView(
        gLSurfaceView: GLSurfaceView
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
            gLSurfaceView,
            touchReceiverCalculator,
            rotationReceiverCalculator,
            scaleReceiverCalculator,
            moveReceiverCalculator,
        )
        val touchListener = TouchListener(touchListenerReceiver)
        gLSurfaceView.setOnTouchListener(touchListener)
    }

    fun prepareGlSurfaceView(gLSurfaceView: GLSurfaceView) {
        gLSurfaceView.apply {
            assignTouchListenerToGLSurfaceView(this)
            setEGLContextClientVersion(2)
            setRenderer(gameRenderer)
        }
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