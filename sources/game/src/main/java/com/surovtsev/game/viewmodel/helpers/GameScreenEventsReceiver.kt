package com.surovtsev.game.viewmodel.helpers

import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.minesweeper.gamelogic.helpers.GameStatusWithElapsedFlow
import com.surovtsev.game.models.game.gamestatus.GameStatusHelper
import com.surovtsev.utils.coroutines.CustomCoroutineScope
import com.surovtsev.utils.coroutines.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.subscriptions.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameScreenEventsReceiver @Inject constructor(
    @Named(GameScreenEventsNames.ShowDialog)
    private val showDialog: ShowDialogEvent,
    private val gameStatusWithElapsedFlow: GameStatusWithElapsedFlow,
    subscriber: Subscriber,
): Subscription {
    init {
        subscriber
            .addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
            gameStatusWithElapsedFlow.collectLatest {
                if (GameStatusHelper.isGameOver(it.gameStatus))
                    withContext(Dispatchers.Main) {
                        showDialog.onDataChanged(true)
                    }
            }
        }
    }
}