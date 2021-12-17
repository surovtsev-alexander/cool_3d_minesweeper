package com.surovtsev.timespan.dagger

import com.surovtsev.utils.coroutines.CustomCoroutineScope
import com.surovtsev.utils.timers.TimeSpan
import com.surovtsev.utils.timers.TimeSpanHelperImp
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@TimeSpanScope
@Component( modules = [
    TimeSpanModule::class
])
interface TimeSpanComponent {

    @DefineComponent.Builder
    interface Builder {
        fun customCoroutineScope(@BindsInstance customCoroutineScope: CustomCoroutineScope): Builder
        fun build(): TimeSpanComponent
    }

    val timeSpan: TimeSpan
}

@Module
object TimeSpanModule {
    @TimeSpanScope
    @Provides
    fun provideTimeSpan(
        timeSpanHelper: TimeSpanHelperImp,
    ): TimeSpan {
        return TimeSpan(
            1000L,
            timeSpanHelper,
        )
    }
}

