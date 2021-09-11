package com.surovtsev.cool_3d_minesweeper.views.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.tooling.preview.Preview
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme

class GameActivityV2: ComponentActivity() {
    private val modelView = GameActivityModelView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Test_composeTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        MinesweeperView()
                    }
                    Row(

                    ) {
                        Controls()
                    }
                }
            }
        }
    }
}

@Composable
fun MinesweeperView() {

}

@Preview
@Composable
fun Controls() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(2f),
            Arrangement.Center
        ) {
            ControlButtons()
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            ControlCheckBox()

        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            GameInfo()

        }
    }
}

@Composable
fun ControlButtons() {
    Row() {
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("V")
        }
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("O")
        }
    }
}

@Composable
fun ControlCheckBox() {
    Row(
        Modifier.fillMaxWidth(),
    ) {
        Checkbox(
            checked = true,
            onCheckedChange = { }
        )
        Text(
            "marking"
        )
    }
}

@Composable
fun GameInfo() {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Text("0")
        Text("0:00")
    }
}

