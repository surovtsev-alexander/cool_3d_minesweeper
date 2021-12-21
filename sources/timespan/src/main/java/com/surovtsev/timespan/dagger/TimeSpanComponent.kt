package com.surovtsev.timespan.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.utils.coroutines.CustomCoroutineScope
import com.surovtsev.utils.timers.TimeSpan
import com.surovtsev.utils.timers.TimeSpanHelperImp
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@TimeSpanScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
   ],
    modules = [
        TimeSpanModule::class,
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
    ): TimeSpan {
        return TimeSpan(
            1000L,
            timeSpanHelper,
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
}

