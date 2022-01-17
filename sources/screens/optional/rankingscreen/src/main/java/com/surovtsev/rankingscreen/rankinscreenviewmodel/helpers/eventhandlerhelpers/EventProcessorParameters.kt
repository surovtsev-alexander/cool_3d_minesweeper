package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.eventhandlerhelpers

import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.rankingscreen.dagger.RankingScreenScope
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.typealiases.RankingScreenStateHolder
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import javax.inject.Inject

@RankingScreenScope
class EventProcessorParameters @Inject constructor(
    val stateHolder: RankingScreenStateHolder,
    val rankingListHelper: RankingListHelper,
    val timeSpanComponent: TimeSpanComponent,
    val restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    val settingsDao: SettingsDao,
    val rankingDao: RankingDao,
    val saveController: SaveController,
)
