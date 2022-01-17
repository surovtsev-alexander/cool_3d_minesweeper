package com.surovtsev.core.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.core.viewmodel.helpers.FinishActionHolder
import com.surovtsev.core.viewmodel.helpers.TemplateScreenViewModelEventChecker
import com.surovtsev.core.viewmodel.helpers.TemplateScreenViewModelEventProcessor
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.finitestatemachine.stateholder.StateHolderImp
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import logcat.logcat

abstract class TemplateScreenViewModel<E: EventToViewModel, D: ScreenData>(
    final override val mandatoryEvents: EventToViewModel.MandatoryEvents<E>,
    final override val noScreenData: D,
    initialState: State<D>,
):
    ViewModel(),
    ErrorDialogPlacer<E, D>,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl(),
    DefaultLifecycleObserver
{
    val finishActionHolder = FinishActionHolder()

    protected val stateHolder: StateHolder<D> = StateHolderImp(
        initialState,
        true
    )
    override val screenStateFlow: ScreenStateFlow<D>
        get() = stateHolder.state

    private val templateScreenViewModelEventHandler = EventHandler(
        TemplateScreenViewModelEventChecker<E, D>(
            mandatoryEvents.closeErrorAndFinish
        ),
        TemplateScreenViewModelEventProcessor<E, D>(
            stateHolder,
            finishActionHolder,
            noScreenData,
        )
    )

    abstract val eventHandler: EventHandler<E, D>

    abstract suspend fun processEvent(event: E): EventProcessingResult<E>

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        handleEvent(
            mandatoryEvents.handleScreenLeavingEventFactory(owner)
        )
    }

    override fun handleEvent(event: E) {
        launchOnIOThread {
            logcat { "handleEvent: $event" }

            val errorMessage = when (
                val eventCheckingResult =
                    templateScreenViewModelEventHandler.eventChecker.check(
                        event, stateHolder.state.value
                    )
            ) {
                is EventCheckerResult.Pass -> {
                    organizeEventProcessing(event)
                    null
                }
                is EventCheckerResult.Skip -> {
                    null
                }
                is EventCheckerResult.Unchecked -> {
                    "internal error 1"
                }
                is EventCheckerResult.RaiseError -> {
                    eventCheckingResult.message
                }
                is EventCheckerResult.ChangeWith -> {
                    organizeEventProcessing(
                        eventCheckingResult.event
                    )
                    null
                }
                else -> {
                    "internal error 2"
                }
            }

            if (errorMessage != null) {
                stateHolder.publishErrorState(
                    errorMessage
                )
            }
        }
    }

    private suspend fun organizeEventProcessing(event: E) {
        // TODO: 17.01.2022 Legacy solution. Need to be deleted.
        if (event.setLoadingStateBeforeProcessing) {
            stateHolder.publishLoadingState()
        }

        when (val eventProcessingResult = processEvent(event)) {
            is EventProcessingResult.Processed -> {}
            is EventProcessingResult.Unprocessed -> {
                stateHolder.publishErrorState("unable to process internal event")
            }
            is EventProcessingResult.PushNewEvent<E> -> {
                return handleEvent(
                    eventProcessingResult.event
                )
            }
        }
    }

//    protected inline fun <reified T: D> processIfDataNullable(
//        checkData: (screenData: T?) -> Boolean,
//        errorAction: (screenData: D?) -> Unit,
//        action: (screenData: T?) -> Unit
//    ) {
//        val screenData = dataHolder.value?.screenData
//        val castedScreenData = screenData as? T
//
//        if (castedScreenData == null || !checkData(castedScreenData)) {
//            errorAction(screenData)
//        } else {
//            action(castedScreenData)
//        }
//    }
//
//    protected inline fun <reified T: D>processIfData(
//        checkData: (screenData: T) -> Boolean,
//        errorAction: () -> Unit,
//        action: (screenData: T) -> Unit
//    ) {
//        val screenData =  dataHolder.value?.screenData
//        val castedScreenData = screenData as? T
//
//        if (castedScreenData == null || !checkData(castedScreenData)) {
//            errorAction()
//        } else {
//            action(castedScreenData)
//        }
//    }
}
