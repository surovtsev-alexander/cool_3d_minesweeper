package com.surovtsev.cool_3d_minesweeper.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
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
    }

    override fun onResume() {
        super.onResume()
        btn_load_game.isEnabled = SaveController(this).hasSave()
    }

    fun startGame(loadGame: Boolean) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(GameActivity.LoadGame, loadGame)
        startActivity(intent)
    }

}
