package com.surovtsev.cool_3d_minesweeper.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme


class MainActivityV2: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainMenuButtons()
        }
    }

    @Preview
    @Composable
    fun MainMenuButtons() {
        val loadGameEnabled = {
            ApplicationController.instance.saveController.hasData(
                SaveTypes.SaveGameJson
            )
        }

        val buttonsParameters = arrayOf(
            "load game" to this::loadGame to loadGameEnabled,
            "new game" to this::startNewGame to null,
            "ranking" to this::openRanking to null,
            "settings" to this::openSettings to null
        )

        Test_composeTheme {
            Surface(color = MaterialTheme.colors.background) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        buttonsParameters.map { (na, e) ->
                            MainMenuButton(
                                na.first,
                                na.second,
                                e
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }

    @Composable
    fun MainMenuButton(
        caption: String,
        action: () -> Unit,
        enabled: (() -> Boolean)?
    ) {
        Button(
            onClick = action,
            Modifier.fillMaxWidth(fraction=0.75f),
            enabled = true
        ) {
            Text(caption)
        }
    }

    private fun loadGame() {
        startGame(true)
    }

    private fun startNewGame() {
        startGame(false)
    }

    private fun openRanking() {
        startActivityHelper(RankingActivity::class.java)
    }

    private fun openSettings() {
        startActivityHelper(SettingsActivity::class.java)
    }

    private fun <T> startActivityHelper(x: Class<T>) {
        startActivity(
            Intent(this, x)
        )
    }

    private fun startGame(loadGame: Boolean) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(GameActivity.LoadGame, loadGame)
        startActivity(intent)
    }

}