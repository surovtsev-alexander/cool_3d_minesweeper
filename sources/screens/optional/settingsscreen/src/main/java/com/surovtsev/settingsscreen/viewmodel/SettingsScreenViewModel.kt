package com.surovtsev.settingsscreen.viewmodel

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.core.viewmodel.*
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.settingsscreen.dagger.DaggerSettingsComponent
import com.surovtsev.settingsscreen.viewmodel.helpers.EventCheckerImp
import com.surovtsev.settingsscreen.viewmodel.helpers.EventProcessorImp
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

typealias SettingsScreenStateFlow = ScreenStateFlow<SettingsScreenData>

typealias SettingsScreenEventHandler = EventHandler<EventToSettingsScreenViewModel>

typealias SettingsScreenErrorDialogPlacer = ErrorDialogPlacer<EventToSettingsScreenViewModel, SettingsScreenData>

class SettingsScreenViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel<EventToSettingsScreenViewModel, SettingsScreenData>(
        EventToSettingsScreenViewModel.MandatoryEvents,
        SettingsScreenData.NoData,
        SettingsScreenInitialState,
    ),
    DefaultLifecycleObserver
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<SettingsScreenViewModel>

    private val settingsComponent = DaggerSettingsComponent
        .builder()
        .appComponentEntryPoint(appComponentEntryPoint)
        .build()

    override val eventHandler = com.surovtsev.finitestatemachine.eventhandler.EventHandler(
        EventCheckerImp(),
        EventProcessorImp(
            settingsComponent,
            stateHolder,
        ),
    )

    override suspend fun processEvent(event: EventToSettingsScreenViewModel): EventProcessingResult<EventToSettingsScreenViewModel> {
        return eventHandler.eventProcessor.processEvent(event)
    }
}
