package com.surovtsev.timespan.dagger

import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.SubscriberImp
import com.surovtsev.utils.timers.TimeSpan
import com.surovtsev.utils.timers.TimeSpanHelper
import com.surovtsev.utils.timers.TimeSpanHelperImp
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
        timeSpanHelper: TimeSpanHelperImp,
        subscriber: Subscriber,
    ): TimeSpan {
        return TimeSpan(
            1000L,
            timeSpanHelper,
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
    ): TimeSpanHelperImp {
        return TimeSpanHelperImp()
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
    fun bindTimeSpanHelper(
        timeSpanHelperImp: TimeSpanHelperImp
    ): TimeSpanHelper

    @Binds
    @TimeSpanScope
    fun bindSubscriber(
        coroutineScopeSubscriberImp: SubscriberImp
    ): Subscriber
}
