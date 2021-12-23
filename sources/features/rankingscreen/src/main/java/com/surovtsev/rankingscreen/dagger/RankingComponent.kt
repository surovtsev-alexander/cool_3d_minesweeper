package com.surovtsev.rankingscreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import dagger.Component


@RankingScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
        TimeSpanComponentEntryPoint::class,
    ],
    modules = [
    ]
)
interface RankingComponent {

    val settingsDao: SettingsDao
    val rankingDao: RankingDao
    val rankingListHelper: RankingListHelper

    val saveController: SaveController
}
