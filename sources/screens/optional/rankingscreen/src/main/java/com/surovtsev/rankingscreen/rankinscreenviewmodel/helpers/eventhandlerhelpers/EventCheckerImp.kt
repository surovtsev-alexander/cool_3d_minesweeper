package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.eventhandlerhelpers

import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.rankingscreen.dagger.RankingScreenScope
import com.surovtsev.rankingscreen.rankinscreenviewmodel.EventToRankingScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenData
import javax.inject.Inject

@RankingScreenScope
class EventCheckerImp @Inject constructor(
): EventChecker<EventToRankingScreenViewModel, RankingScreenData>
{
    override fun check(
        event: EventToRankingScreenViewModel
        , state: State<RankingScreenData>
    ): EventCheckerResult<EventToRankingScreenViewModel> {
        return EventCheckerResult.Pass()
    }
}