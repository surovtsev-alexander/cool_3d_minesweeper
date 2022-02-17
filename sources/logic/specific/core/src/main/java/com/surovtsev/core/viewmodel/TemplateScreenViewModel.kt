package com.surovtsev.core.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.core.viewmodel.finitestatemachine.eventhandler.TemplateScreenViewModelEventHandler
import com.surovtsev.core.viewmodel.helpers.FinishActionHolder
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
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

    private val templateScreenViewModelEventHandler = TemplateScreenViewModelEventHandler(
        mandatoryEvents.closeErrorAndFinish,
        stateHolder,
        finishActionHolder,
        noScreenData,
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
            val handlingResult = eventHandles.map {
                it.handleEvent(event, currState)
            }

            handlingResult.firstOrNull {
                it !is EventHandlingResult.Skip
            } ?: return@launchOnIOThread

            handlingResult.firstOrNull {
                it is EventHandlingResult.RaiseError
            } ?.let {
                stateHolder.publishErrorState(
                    (it as EventHandlingResult.RaiseError<E>).message
                )
                return@launchOnIOThread
            }

            val changeEventResults = handlingResult.filterIsInstance<EventHandlingResult.ChangeWith<E>>()

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

            val processingResults = handlingResult.map {
                if (it is EventHandlingResult.Process) {
                    it.eventProcessor.invoke()
                } else {
                    null
                }
            }

            val pushNewEventResults = processingResults
                .filterIsInstance<EventProcessingResult.Ok<E>>()
                .filter { it.newEventToPush != null }
                .map { it.newEventToPush!! }

            when (pushNewEventResults.count()) {
                0 -> {
                }
                1 -> {
                    return@launchOnIOThread receiveEvent(
                        pushNewEventResults[0]
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
}
