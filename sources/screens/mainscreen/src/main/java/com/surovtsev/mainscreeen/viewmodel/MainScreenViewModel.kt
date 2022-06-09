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


package com.surovtsev.mainscreeen.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.mainscreeen.dagger.DaggerMainScreenComponent
import com.surovtsev.mainscreeen.presentation.ButtonsInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import logcat.logcat


class MainScreenViewModel @AssistedInject constructor(
    @Assisted @Suppress("UNUSED_PARAMETER") savedStateHandle: SavedStateHandle,
    @Assisted @Suppress("UNUSED_PARAMETER") context: Context,
    @Assisted appComponentEntryPoint: AppComponentEntryPoint,
):
    ViewModel(),
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<MainScreenViewModel>

    private val _hasSave = MutableStateFlow(false)
    val hasSave: StateFlow<Boolean> = _hasSave.asStateFlow()

    object ButtonNames {
        const val NewGame   = "new game"
        const val LoadGame  = "load game"
        const val Ranking   = "ranking"
        const val Settings  = "settings"
        const val Help      = "help"
    }

    val buttonsInfo: ButtonsInfo

    private val saveController: SaveController

    init {
        val mainScreenComponent = DaggerMainScreenComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .build()

        buttonsInfo = mainScreenComponent.buttonInfo
        saveController = mainScreenComponent.saveController
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        _hasSave.value = saveController.hasData(
            SaveTypes.SaveGameJson
        )

        logcat { "onResume; hasSave.value: ${hasSave.value}" }
    }
}
