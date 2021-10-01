package com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder

import android.content.Context
import androidx.lifecycle.Lifecycle
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
        private var lifecycle: Lifecycle? = null

        fun isNeededToCreateComponent(
            lifecycle: Lifecycle
        ): Boolean {
            return this.lifecycle != lifecycle || this.component == null
        }

        fun storeComponent(
            component: T,
            lifecycle: Lifecycle
        ) {
            forgotComponent()
            this.component = component
            this.lifecycle = lifecycle
        }

        private fun forgotComponent() {
            this.component = null
            this.lifecycle = null
        }
    }

    val rankingComponentHolder = ComponentHolder<RankingComponent>()
    val settingsComponentHolder = ComponentHolder<SettingsComponent>()
    val gameComponentHolder = ComponentHolder<GameComponent>()

    fun createRankingComponentIfNeeded(
        lifecycle: Lifecycle
    ): Boolean {
        val res = rankingComponentHolder.isNeededToCreateComponent(lifecycle)
        if (res) {
            rankingComponentHolder.storeComponent(
                appComponent
                    .rankingComponent(),
                lifecycle
            )
        }
        return res
    }

    fun createSettingsComponentIfNeeded(
        lifecycle: Lifecycle
    ): Boolean {
        val res = settingsComponentHolder.isNeededToCreateComponent(lifecycle)
        if (res) {
            settingsComponentHolder.storeComponent(
                appComponent
                    .settingComponent(),
                lifecycle
            )
        }
        return res
    }

    fun createGameComponentIfNeeded(
        loadGame: Boolean,
        lifecycle: Lifecycle
    ): Boolean {
        val res = gameComponentHolder.isNeededToCreateComponent(lifecycle)
        if (res) {
            gameComponentHolder.storeComponent(
                appComponent
                    .gameComponent()
                    .loadGame(loadGame)
                    .build(),
                lifecycle
            )
        }
        return res
    }
}
