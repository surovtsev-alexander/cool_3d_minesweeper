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
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
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
