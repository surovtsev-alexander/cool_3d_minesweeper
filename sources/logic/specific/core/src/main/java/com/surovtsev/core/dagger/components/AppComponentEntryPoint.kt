package com.surovtsev.core.dagger.components

import android.content.Context
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController

interface AppComponentEntryPoint {
    val context: Context

    val settingsDao: SettingsDao
    val rankingDao: RankingDao

    val rankingListHelper: RankingListHelper

    val saveController: SaveController
}