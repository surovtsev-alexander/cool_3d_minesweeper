package com.surovtsev.rankingscreen.rankinscreenviewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.viewmodel.CommandFromScreen

sealed class CommandFromRankingScreen(
    override val setLoadingStateWhileProcessing: Boolean = true
): CommandFromScreen {
    class HandleScreenLeaving(
        override val owner: LifecycleOwner
    ):
        CommandFromRankingScreen(),
        CommandFromScreen.HandleScreenLeaving

    object LoadData: CommandFromRankingScreen(), CommandFromScreen.Init

    object CloseError: CommandFromRankingScreen(), CommandFromScreen.CloseError

    object CloseErrorAndFinish: CommandFromRankingScreen(), CommandFromScreen.CloseErrorAndFinish

    class FilterList(
        val selectedSettingsId: Long
    ): CommandFromRankingScreen()

    open class SortList(
        val rankingTableSortParameters: RankingTableSortParameters
    ): CommandFromRankingScreen()

    class SortListWithNoDelay(
        rankingTableSortParameters: RankingTableSortParameters
    ): SortList(
        rankingTableSortParameters
    )

    object BaseCommands: CommandFromScreen.BaseCommands<CommandFromRankingScreen>(
        LoadData,
        CloseError,
        CloseErrorAndFinish,
        { HandleScreenLeaving(it) },
    )
}
