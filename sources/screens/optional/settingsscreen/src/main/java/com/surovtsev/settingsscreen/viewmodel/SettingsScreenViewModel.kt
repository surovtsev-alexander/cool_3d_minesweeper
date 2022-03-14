package com.surovtsev.settingsscreen.viewmodel

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.viewmodel.TemplateScreenViewModel
import com.surovtsev.settingsscreen.dagger.DaggerSettingsScreenComponent
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.EventToSettingsScreenViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SettingsScreenViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel(
        EventToSettingsScreenViewModel.MandatoryEvents,
    ),
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<SettingsScreenViewModel>

    private val settingsScreenComponent = DaggerSettingsScreenComponent
        .builder()
        .appComponentEntryPoint(appComponentEntryPoint)
        .stateHolder(stateHolder)
        .finiteStateMachineFactory(::createFiniteStateMachine)
        .build()

    override val finiteStateMachine =
        settingsScreenComponent
            .finiteStateMachine
}
