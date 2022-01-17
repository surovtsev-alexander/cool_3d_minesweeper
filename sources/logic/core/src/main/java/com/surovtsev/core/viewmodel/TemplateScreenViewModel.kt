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

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        receiveEvent(
            mandatoryEvents.handleScreenLeavingEventFactory(owner)
        )
    }

    override fun receiveEvent(event: E) {
        val eventHandles = arrayOf(
            eventHandler,
            templateScreenViewModelEventHandler,
        )

        launchOnIOThread {
            logcat { "handleEvent: $event" }

            val currState = stateHolder.state.value
            val checkingResult = eventHandles.map {
                it.eventChecker.check(event, currState)
            }

            checkingResult.firstOrNull {
                it !is EventCheckerResult.Skip
            } ?: return@launchOnIOThread

            checkingResult.firstOrNull {
                it is EventCheckerResult.RaiseError
            } ?.let {
                stateHolder.publishErrorState(
                    (it as EventCheckerResult.RaiseError<E>).message
                )
                return@launchOnIOThread
            }

            val changeEventResults = checkingResult.filterIsInstance<EventCheckerResult.ChangeWith<E>>()

            val eventToProcess = when (changeEventResults.count()) {
                0 -> {
                    event
                }
                1 -> {
                    changeEventResults[0].event
                }
                else -> {
                    stateHolder.publishErrorState(
                        "internal error 1"
                    )
                    return@launchOnIOThread
                }
            }

            if (eventToProcess.setLoadingStateBeforeProcessing) {
                stateHolder.publishLoadingState()
            }

            val processingResults = eventHandles.map {
                it.eventProcessor.processEvent(eventToProcess)
            }

            val pushNewEventResults = processingResults.filterIsInstance<EventProcessingResult.PushNewEvent<E>>()

            when (pushNewEventResults.count()) {
                0 -> {
                }
                1 -> {
                    return@launchOnIOThread receiveEvent(
                        pushNewEventResults[0].event
                    )
                }
                else -> {
                    // TODO: 17.01.2022 do not fix it.
                    //  it is temporary solution to migrate to finite state machine.
                    stateHolder.publishErrorState(
                        "internal error 3"
                    )
                }
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
