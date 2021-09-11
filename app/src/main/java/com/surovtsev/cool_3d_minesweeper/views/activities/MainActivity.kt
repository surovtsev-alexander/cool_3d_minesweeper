package com.surovtsev.cool_3d_minesweeper.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.surovtsev.cool_3d_minesweeper.model_views.MainActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.theme.GrayBackground

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainMenuButtons(modelView)
        }
    }

    private val modelView = MainActivityModelView(this)


    override fun onResume() {
        super.onResume()
        modelView.invalidate()
    }

    override fun onRestart() {
        super.onRestart()
        modelView.invalidate()
    }
}

@Composable
fun MainMenuButtons(modelView: MainActivityModelView) {
    val enabled: Boolean by modelView.hasSave.data.observeAsState(false)

    Test_composeTheme {
        Surface(color = MaterialTheme.colors.background) {
            Box(
                Modifier.background(GrayBackground)//Color(0xFF48cae4))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        modelView.buttonsParameters.map { (n, a) ->
                            MainMenuButton(
                                n,
                                a,
                                if (modelView.isLoadGameAction(a)) enabled else true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenuButton(
    caption: String,
    action: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = action,
        Modifier
            .fillMaxWidth(fraction=0.75f)
            .border(1.dp, Color.Black),
        enabled
    ) {
        Text(caption)
    }
}