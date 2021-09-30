package com.surovtsev.cool_3d_minesweeper.dagger.app

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.ranking.RankingComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.settings.SettingsComponent
import com.surovtsev.cool_3d_minesweeper.model_views.main_activity_view_model.MainActivityViewModel
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.minesweeper.database.IDBHelper
import dagger.*

@AppScope
@Component(
    modules = [
        AppBindModule::class
    ]
)
interface AppComponent {
    val mainActivityViewModel: MainActivityViewModel


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun gameComponent(): GameComponent.Builder
    fun rankingComponent(): RankingComponent
    fun settingComponent(): SettingsComponent
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
