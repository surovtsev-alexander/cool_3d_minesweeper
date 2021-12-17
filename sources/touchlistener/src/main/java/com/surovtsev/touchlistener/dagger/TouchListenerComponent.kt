package com.surovtsev.touchlistener.dagger

import com.surovtsev.touchlistener.TouchListener
import com.surovtsev.touchlistener.helpers.ClickAndRotationHelper
import com.surovtsev.touchlistener.helpers.ScalingHelper
import com.surovtsev.touchlistener.helpers.TouchHelper
import com.surovtsev.touchlistener.helpers.TouchReceiverImp
import com.surovtsev.touchlistener.helpers.handlers.MoveHandler
import com.surovtsev.touchlistener.helpers.handlers.TouchHandler
import com.surovtsev.touchlistener.helpers.receivers.TouchReceiver
import com.surovtsev.utils.timers.TimeSpanHelper
import dagger.*
import glm_.vec2.Vec2
import javax.inject.Named

@TouchListenerScope
@Component(
    modules = [
        TouchListenerBindModule::class,
        TouchHelperModule::class,
        ScalingHelperModule::class,
        ClickAndRotationHelperModule::class,
    ]
)
interface TouchListenerComponent {
    val touchListener: TouchListener

    @Component.Builder
    interface Builder {
        fun touchHandler(@BindsInstance touchHandler: TouchHandler): Builder

        fun moveHandler(@BindsInstance moveHandler: MoveHandler): Builder

        fun timeSpanHelper(@BindsInstance timeSpanHelper: TimeSpanHelper): Builder

        fun build(): TouchListenerComponent
    }
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
    fun bindRotationReceiver(
        moveHandler: MoveHandler
    ): com.surovtsev.touchlistener.helpers.receivers.RotationReceiver

    @TouchListenerScope
    @Binds
    fun bindScaleReceiver(
        moveHandler: MoveHandler
    ): com.surovtsev.touchlistener.helpers.receivers.ScaleReceiver

    @TouchListenerScope
    @Binds
    fun bindMoveReceiver(
        moveHandler: MoveHandler
    ): com.surovtsev.touchlistener.helpers.receivers.MoveReceiver

    @TouchListenerScope
    @Binds
    fun bindTouchReceiver(
        touchReceiver: TouchReceiverImp
    ): TouchReceiver
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