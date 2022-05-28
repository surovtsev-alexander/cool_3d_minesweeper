package com.surovtsev.restartablecoroutinescope.dagger

import com.surovtsev.utils.coroutines.customcoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.SubscriberImp
import com.surovtsev.utils.dagger.components.RestartableCoroutineScopeEntryPoint
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@RestartableCoroutineScopeDaggerScope
@Component(
    dependencies = [
    ],
    modules = [
        RestartableCoroutineScopeModule::class,
        RestartableCoroutineScopeBindingModule::class,
    ]
)
interface RestartableCoroutineScopeComponent: RestartableCoroutineScopeEntryPoint {
}

@Module
object RestartableCoroutineScopeModule {
    @RestartableCoroutineScopeDaggerScope
    @Provides
    fun provideCustomCoroutineScope(
    ): RestartableCoroutineScope {
        return RestartableCoroutineScope(
            Dispatchers.IO
        )
    }

    @RestartableCoroutineScopeDaggerScope
    @Provides
    fun provideSubscriberImp(
        restartableCoroutineScope: RestartableCoroutineScope,
    ): SubscriberImp {
        return SubscriberImp(
            restartableCoroutineScope
        )
    }
}

@Module
interface RestartableCoroutineScopeBindingModule {
    @RestartableCoroutineScopeDaggerScope
    @Binds
    fun bindSubscriber(
        coroutineScopeSubscriberImp: SubscriberImp
    ): Subscriber
}