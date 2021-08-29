package com.surovtsev.cool_3d_minesweeper.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.surovtsev.cool_3d_minesweeper.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start_game.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }
}
