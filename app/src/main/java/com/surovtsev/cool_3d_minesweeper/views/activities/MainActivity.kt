package com.surovtsev.cool_3d_minesweeper.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme

import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainMenuButtons()
        }
    }

    private class MainActivityViewModel: ViewModel() {
        private val _hasSave = MutableLiveData<Boolean>(false)
        val hasSave: LiveData<Boolean> = _hasSave

        fun onHasSaveChanged(newVal: Boolean) {
            _hasSave.value = newVal
        }
    }

    private val mainActivityViewModel = MainActivityViewModel()

    private val buttonsParameters = arrayOf(
        "load game" to this::loadGame,
        "new game" to this::startNewGame,
        "ranking" to this::openRanking,
        "settings" to this::openSettings,
    )


    @Preview
    @Composable
    fun MainMenuButtons() {
        val enabled: Boolean by mainActivityViewModel.hasSave.observeAsState(false)

        Test_composeTheme {
            Surface(color = MaterialTheme.colors.background) {
                Box(
                    Modifier.background(Color(0xFF48cae4))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            buttonsParameters.map { (n, a) ->
                                MainMenuButton(
                                    n,
                                    a,
                                    if (a == this@MainActivity::loadGame) enabled else true
                                )
                            }
                        }
                    }
                }
            }
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

    override fun onResume() {
        super.onResume()
        invalidate()
    }

    override fun onRestart() {
        super.onRestart()
        invalidate()
    }

    private fun invalidate() {
        mainActivityViewModel.onHasSaveChanged(
            ApplicationController.instance.saveController.hasData(
                SaveTypes.SaveGameJson
            )
        )
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
    enabled: Boolean
) {
    Button(
        onClick = action,
        Modifier.fillMaxWidth(fraction=0.75f),
        enabled
    ) {
        Text(caption)
    }
}