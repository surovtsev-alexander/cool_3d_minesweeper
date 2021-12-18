package com.surovtsev.core.dagger.components

import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController

interface RootComponent {
    val settingsDao: SettingsDao
    val rankingDao: RankingDao

    val rankingListHelper: RankingListHelper

    val saveController: SaveController
}