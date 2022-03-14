package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.eventhandler

import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.rankingscreen.dagger.RankingScreenScope
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import javax.inject.Inject

@RankingScreenScope
class EventHandlerParameters @Inject constructor(
    val stateHolder: StateHolder,
    val rankingListHelper: RankingListHelper,
    val timeSpanComponent: TimeSpanComponent,
    val restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    val settingsDao: SettingsDao,
    val rankingDao: RankingDao,
    val saveController: SaveController,
)
