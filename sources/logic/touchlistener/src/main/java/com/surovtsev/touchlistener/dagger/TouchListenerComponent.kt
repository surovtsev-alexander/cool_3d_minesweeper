package com.surovtsev.touchlistener.dagger

import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.touchlistener.TouchListener
import com.surovtsev.touchlistener.helpers.ClickAndRotationHelper
import com.surovtsev.touchlistener.helpers.ScalingHelper
import com.surovtsev.touchlistener.helpers.TouchHelper
import com.surovtsev.touchlistener.helpers.TouchReceiverImp
import com.surovtsev.touchlistener.helpers.handlers.MoveHandler
import com.surovtsev.touchlistener.helpers.holders.HandlersHolder
import com.surovtsev.touchlistener.helpers.holders.HandlersHolderImp
import com.surovtsev.touchlistener.helpers.receivers.TouchReceiver
import dagger.*
import glm_.vec2.Vec2
import javax.inject.Named

@TouchListenerScope
@Component(
    dependencies = [
        TimeSpanComponentEntryPoint::class,
    ],
    modules = [
        TouchListenerBindModule::class,
        TouchHelperModule::class,
        ScalingHelperModule::class,
        ClickAndRotationHelperModule::class,
    ]
)
interface TouchListenerComponent {
    val touchListener: TouchListener
}

@Module
interface TouchListenerBindModule {
    @TouchListenerScope
    @Binds
    fun bindTouchHelper(
        clickAndRotationHelper: ClickAndRotationHelper
    ): TouchHelper

    @TouchListenerScope
    @Binds
    fun bindTouchReceiver(
        touchReceiver: TouchReceiverImp
    ): TouchReceiver

    @TouchListenerScope
    @Binds
    fun bindHandlersHolder(
        handlersHolderImp: HandlersHolderImp
    ): HandlersHolder
}

@Module
object TouchHelperModule {
    @Named(TouchListener.PrevPointerCount)
    @TouchListenerScope
    @Provides
    fun providePrevPointerCount() = 0

    @TouchListenerScope
    @Provides
    @Named(ScalingHelper.PrevDistance)
    fun getPrevDistance() = 0f
}

@Module
object ScalingHelperModule {
    @Provides
    @Named(TouchHelper.PrevCenter)
    fun providePrevCenter() = Vec2()
}

@Module
object ClickAndRotationHelperModule {
    @Provides
    @Named(ClickAndRotationHelper.Prev)
    fun providePrev() = Vec2()

    @Provides
    @Named(ClickAndRotationHelper.Movement)
    fun provideMovement() = 0f

    @Provides
    @Named(ClickAndRotationHelper.Downed)
    fun provideDowned() = false
}