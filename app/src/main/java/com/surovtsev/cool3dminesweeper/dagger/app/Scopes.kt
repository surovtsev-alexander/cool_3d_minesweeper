package com.surovtsev.cool3dminesweeper.dagger.app

import javax.inject.Scope

//@Scope
//@Retention(AnnotationRetention.RUNTIME)
//annotation class AppScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MainScreenScope
