package com.surovtsev.subscriptionsholder.dagger

import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolderWithName
import com.surovtsev.utils.dagger.components.RestartableCoroutineScopeEntryPoint
import com.surovtsev.utils.dagger.components.SubscriptionsHolderEntryPoint
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@SubscriptionsHolderScope
@Component(
    dependencies = [
        RestartableCoroutineScopeEntryPoint::class,
    ],
    modules = [
        SubscriptionsHolderModule::class,
    ]
)
interface SubscriptionsHolderComponent: SubscriptionsHolderEntryPoint {

    @Component.Builder
    interface Builder {
        fun restartableCoroutineScopeEntryPoint(
            restartableCoroutineScopeEntryPoint: RestartableCoroutineScopeEntryPoint
        ): Builder

//        @Named(SubscriptionsHolder.NAME)
        @BindsInstance
        fun subscriptionsHolderName(name: String): Builder


        fun build(): SubscriptionsHolderComponent
    }
}

@Module
object SubscriptionsHolderModule {
    @SubscriptionsHolderScope
    @Provides
    fun provideSubscriptionsHolder(
        customCoroutineScope: CustomCoroutineScope,
    ): SubscriptionsHolder {
        return SubscriptionsHolder(customCoroutineScope)
    }

    @SubscriptionsHolderScope
    @Provides
    fun provideSubscriptionsHolderWithName(
        subscriptionsHolder: SubscriptionsHolder,
//        @Named(SubscriptionsHolder.NAME)
        name: String,
    ): SubscriptionsHolderWithName {
        return SubscriptionsHolderWithName(
            subscriptionsHolder,
            name
        )
    }
}