package com.surovtsev.cool_3d_minesweeper.dagger.app

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool_3d_minesweeper.model_views.HasSaveEvent
import com.surovtsev.cool_3d_minesweeper.model_views.MainActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.activities.MainActivity
import com.surovtsev.cool_3d_minesweeper.views.activities.RankingActivity
import com.surovtsev.cool_3d_minesweeper.views.activities.SettingsActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Scope

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun gameComponent(): GameComponent.Builder

    fun inject(mainActivity: MainActivity)
    fun inject(settingsActivity: SettingsActivity)
    fun inject(rankingActivity: RankingActivity)
}

@Module
object AppModule {
    @AppScope
    @Named(MainActivityModelView.HasSaveEventName)
    @Provides
    fun provideHasSaveEventName(): HasSaveEvent = HasSaveEvent(false)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope
