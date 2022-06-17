/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.rankingscreen.rankinscreenviewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.rankingscreen.dagger.DaggerRankingScreenComponent
import com.surovtsev.rankingscreen.dagger.RankingScreenComponent
import com.surovtsev.templateviewmodel.TemplateViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class RankingScreenViewModel @AssistedInject constructor(
    @Suppress("UNUSED_PARAMETER") @Assisted savedStateHandle: SavedStateHandle,
    @Suppress("UNUSED_PARAMETER") @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateViewModel()
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<RankingScreenViewModel>

    private val rankingScreenComponent: RankingScreenComponent =
        DaggerRankingScreenComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .stateHolder(stateHolder)
            .finiteStateMachineFactory(::createFiniteStateMachine)
            .build()

    override val finiteStateMachine = rankingScreenComponent
        .finiteStateMachine

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        rankingScreenComponent
            .restartableCoroutineScopeComponent
            .subscriberImp
            .restart()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        rankingScreenComponent.restartableCoroutineScopeComponent.subscriberImp.stop()
    }
}
