package com.surovtsev.cool_3d_minesweeper.dagger.app.ranking

import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@RankingScope
@Subcomponent(
    modules = [
        RankingModule::class
    ]
)
interface RankingComponent {

}

@Module
object RankingModule {

}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class RankingScope