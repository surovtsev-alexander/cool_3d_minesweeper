package com.surovtsev.gamelogic.dagger

import dagger.Component
import dagger.Module

@GameLogicScope
@Component
interface GameLogicComponent {

    @Component.Builder
    interface Builder {

        fun build(): GameLogicComponent
    }
}


@Module
object GameLogicComponentModule {

}
