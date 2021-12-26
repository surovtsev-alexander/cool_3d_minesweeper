package com.surovtsev.timespan.dagger

import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.SubscriberImp
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@TimeSpanScope
@Component(
    dependencies = [
   ],
    modules = [
        TimeSpanModule::class,
        TimeSpanBindingsModule::class,
    ]
)
interface TimeSpanComponent: TimeSpanComponentEntryPoint {
}

@Module
object TimeSpanModule {
    @TimeSpanScope
    @Provides
    fun provideTimeSpan(
        timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
        subscriber: Subscriber,
    ): AsyncTimeSpan {
        return AsyncTimeSpan(
            1000L,
            timeAfterDeviceStartupFlowHolder,
            subscriber,
        )
    }

    @TimeSpanScope
    @Provides
    fun provideCustomCoroutineScope(
    ): CustomCoroutineScope {
        return CustomCoroutineScope(
            Dispatchers.IO
        )
    }

    @TimeSpanScope
    @Provides
    fun provideTimeSpanHelperImp(
    ): ManuallyUpdatableTimeAfterDeviceStartupFlowHolder {
        return ManuallyUpdatableTimeAfterDeviceStartupFlowHolder()
    }

    @TimeSpanScope
    @Provides
    fun provideSubscriberImp(
        customCoroutineScope: CustomCoroutineScope,
    ): SubscriberImp {
        return SubscriberImp(
            customCoroutineScope
        )
    }
}

@Module
interface TimeSpanBindingsModule {
    @Binds
    @TimeSpanScope
    fun bindTimeAfterDeviceStartupFlowHolder(
        manuallyUpdatableTimeAfterDeviceStartupFlowHolder: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
    ): TimeAfterDeviceStartupFlowHolder

    @Binds
    @TimeSpanScope
    fun bindSubscriber(
        coroutineScopeSubscriberImp: SubscriberImp
    ): Subscriber
}
