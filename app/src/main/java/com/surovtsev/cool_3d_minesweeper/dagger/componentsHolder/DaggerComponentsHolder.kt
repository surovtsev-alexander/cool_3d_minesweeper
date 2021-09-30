package com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder

import android.content.Context
import androidx.navigation.NavBackStackEntry
import com.surovtsev.cool_3d_minesweeper.dagger.app.DaggerAppComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.ranking.RankingComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.settings.SettingsComponent

class DaggerComponentsHolder(
    context: Context
) {
    val appComponent = DaggerAppComponent
        .builder()
        .context(context)
        .build()

    class ComponentHolder<T> {
        var component: T? = null
            private set
        private var entry: NavBackStackEntry? = null

        fun isNeededToCreateComponent(
            entry: NavBackStackEntry
        ): Boolean {
            return this.entry != entry || this.component == null
        }

        fun storeComponent(
            component: T,
            entry: NavBackStackEntry
        ) {
            forgotComponent()
            this.component = component
            this.entry = entry
        }

        fun forgotComponent() {
            this.component = null
            this.entry = null
        }
    }

    val rankingComponentHolder = ComponentHolder<RankingComponent>()
    val settingsComponentHolder = ComponentHolder<SettingsComponent>()
    val gameComponentHolder = ComponentHolder<GameComponent>()

    fun createRankingComponentIfNeeded(
        entry: NavBackStackEntry
    ): Boolean {
        val res = rankingComponentHolder.isNeededToCreateComponent(entry)
        if (res) {
            rankingComponentHolder.storeComponent(
                appComponent
                    .rankingComponent(),
                entry
            )
        }
        return res
    }

    fun createSettingsComponentIfNeeded(
        entry: NavBackStackEntry
    ): Boolean {
        val res = settingsComponentHolder.isNeededToCreateComponent(entry)
        if (res) {
            settingsComponentHolder.storeComponent(
                appComponent
                    .settingComponent(),
                entry
            )
        }
        return res
    }

    fun createGameComponentIfNeeded(
        loadGame: Boolean,
        entry: NavBackStackEntry
    ): Boolean {
        val res = gameComponentHolder.isNeededToCreateComponent(entry)
        if (res) {
            gameComponentHolder.storeComponent(
                appComponent
                    .gameComponent()
                    .loadGame(loadGame)
                    .build(),
                entry
            )
        }
        return res
    }

}
