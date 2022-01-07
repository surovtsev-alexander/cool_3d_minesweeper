package com.surovtsev.gamestate.dagger

import dagger.Component
import dagger.Module

@GameStateScope
@Component(
    dependencies = [
    ],
    modules = [
        GameStateModule::class,
    ]
)
interface GameStateComponent {
}

@Module
abstract class GameStateModule {

}