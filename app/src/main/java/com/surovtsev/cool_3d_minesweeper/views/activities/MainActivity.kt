package com.surovtsev.cool_3d_minesweeper.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_load_game.setOnClickListener {
            startGame(true)
        }

        btn_new_game.setOnClickListener {
            startGame(false)
        }

        btn_settings.setOnClickListener {
            ApplicationController.startingActivityCode {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun invalidate() {
        btn_load_game.isEnabled =
            ApplicationController.instance.saveController.hasData(
                SaveTypes.SaveGameJson
            )
    }

    override fun onResume() {
        super.onResume()
        invalidate()
    }

    override fun onRestart() {
        super.onRestart()
        invalidate()
    }

    private fun startGame(loadGame: Boolean) {
        ApplicationController.startingActivityCode {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra(GameActivity.LoadGame, loadGame)
            startActivity(intent)
        }
    }
}
