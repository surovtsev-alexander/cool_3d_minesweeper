package com.surovtsev.cool_3d_minesweeper.dagger

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.GameLogicStateHelper
import dagger.Component
import dagger.Module

@Component(modules = [AppModule::class])
interface AppComponent {

//    val gameLogicStateHelperFactory: GameLogicStateHelper.Factory
}

@Module
object AppModule {

}

