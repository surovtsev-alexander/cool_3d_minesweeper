package com.surovtsev.ranking.rankinscreenviewmodel

import com.surovtsev.core.ranking.RankingTableSortType
import com.surovtsev.core.settings.SettingsList

sealed class RankingScreenState(
    val rankingScreenData: RankingScreenData
) {

    class Loading(
        rankingScreenData: RankingScreenData
    ): RankingScreenState(rankingScreenData)

    class Error(
        rankingScreenData: RankingScreenData,
        val message: String
    ): RankingScreenState(rankingScreenData)

    class Idle(
        rankingScreenData: RankingScreenData
    ): RankingScreenState(rankingScreenData)
}

sealed class RankingScreenData() {

    object NoData: RankingScreenData()

    sealed class SettingsListLoaded(
        val settingsList: SettingsList
    ): RankingScreenData() {

        sealed class SettingsListIsFiltered(
            settingsListLoaded: SettingsListLoaded,
            val selectedSettingsId: Int,
            val filteredSettingsList: SettingsList,
        ): SettingsListLoaded(
            settingsListLoaded.settingsList
        ) {

            sealed class RankingListIsPrepared(
                settingsListIsFiltered: SettingsListIsFiltered,
                val rankingTableSortType: RankingTableSortType,
                val sortedSettingsList: SettingsList,
            ): SettingsListIsFiltered(
                settingsListIsFiltered,
                settingsListIsFiltered.selectedSettingsId,
                settingsListIsFiltered.filteredSettingsList
            )
        }
    }
}
