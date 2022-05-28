package com.surovtsev.timespan.dagger

import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolder
import com.surovtsev.utils.dagger.components.SubscriptionsHolderEntryPoint
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.sync.ManuallyUpdatableTimeAfterDeviceStartupHolder
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@TimeSpanScope
@Component(
    dependencies = [
        SubscriptionsHolderEntryPoint::class,
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
        subscriptionsHolder: SubscriptionsHolder,
    ): AsyncTimeSpan {
        return AsyncTimeSpan(
            1000L,
            timeAfterDeviceStartupFlowHolder,
            subscriptionsHolder,
        )
    }

    @TimeSpanScope
    @Provides
    fun provideManuallyUpdatableTimeAfterDeviceStartupHolder(
    ): ManuallyUpdatableTimeAfterDeviceStartupHolder {
        return ManuallyUpdatableTimeAfterDeviceStartupHolder()
    }

    @TimeSpanScope
    @Provides
    fun provideManuallyUpdatableTimeAfterDeviceStartupFlowHolder(
        manuallyUpdatableTimeAfterDeviceStartupHolder: ManuallyUpdatableTimeAfterDeviceStartupHolder,
    ): ManuallyUpdatableTimeAfterDeviceStartupFlowHolder {
        return ManuallyUpdatableTimeAfterDeviceStartupFlowHolder(
            manuallyUpdatableTimeAfterDeviceStartupHolder
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
}
