package com.surovtsev.cool_3d_minesweeper.dagger.app

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.ranking.RankingComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.settings.SettingsComponent
import com.surovtsev.cool_3d_minesweeper.model_views.main_activity_model_view.HasSaveEvent
import com.surovtsev.cool_3d_minesweeper.model_views.main_activity_model_view.MainActivityModelView
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.minesweeper.database.IDBHelper
import com.surovtsev.cool_3d_minesweeper.views.activities.MainActivity
import dagger.*
import javax.inject.Named

@AppScope
@Component(
    modules = [
        AppModule::class,
        AppBindModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun gameComponent(): GameComponent.Builder
    fun rankingComponent(): RankingComponent
    fun settingComponent(): SettingsComponent

    fun inject(mainActivity: MainActivity)
}

@Module
object AppModule {
    @AppScope
    @Named(MainActivityModelView.HasSaveEventName)
    @Provides
    fun provideHasSaveEvent(): HasSaveEvent = HasSaveEvent(false)
}

@Module
interface AppBindModule {
    @Suppress("unused")
    @AppScope
    @Binds
    fun bindIDBHelper(
        dbHelper: DBHelper
    ): IDBHelper
}
