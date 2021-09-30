package com.surovtsev.cool_3d_minesweeper.dagger.app

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class RankingScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SettingsScope