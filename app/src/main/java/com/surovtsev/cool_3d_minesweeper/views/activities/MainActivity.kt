package com.surovtsev.cool_3d_minesweeper.views.activities

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
import androidx.compose.ui.unit.dp
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme

import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.model_views.*
import com.surovtsev.cool_3d_minesweeper.views.theme.GrayBackground
import javax.inject.Inject
import javax.inject.Named

class MainActivity: ComponentActivity() {

    @Inject
    lateinit var modelView: MainActivityModelView
    @Inject
    @Named(MainActivityModelView.HasSaveEventName)
    lateinit var hasSaveEvent: HasSaveEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        daggerComponentsHolder
            .appComponent
            .inject(this)

        setContent {
            MainMenuButtons(
                hasSaveEvent,
                modelView.buttonsParameters,
                modelView::isLoadGameAction
            )
        }
    }
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
fun MainMenuButtons(
    hasSaveEvent: HasSaveEvent,
    buttonParameters: ButtonParameters,
    isLoadedGameAction: IsLoadedGameAction
) {
    val enabled: Boolean by hasSaveEvent.run {
        data.observeAsState(defaultValue)
    }

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
                        buttonParameters.map { bP ->
                            MainMenuButton(
                                bP,
                                if (isLoadedGameAction(bP.second)) enabled else true
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
    buttonParameter: ButtonParameter,
    enabled: Boolean
) {
    Button(
        onClick = buttonParameter.second,
        Modifier
            .fillMaxWidth(fraction = 0.75f)
            .border(1.dp, Color.Black),
        enabled
    ) {
        Text(buttonParameter.first)
    }
}