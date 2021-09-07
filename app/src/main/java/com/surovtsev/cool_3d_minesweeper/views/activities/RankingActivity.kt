package com.surovtsev.cool_3d_minesweeper.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.RankingDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.RankingData
import kotlinx.android.synthetic.main.activity_ranking.*

class RankingActivity : AppCompatActivity() {
    private val rankingDBQueries: RankingDBQueries = RankingDBQueries(DBHelper(this))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)


        btn_test.setOnClickListener {
            rankingDBQueries.insert(RankingData(3, 100, "1dafd"))
        }
        val rankingList = rankingDBQueries.getRankingList()
        val str = rankingList.map { it.toString() }.fold("") {acc, r -> "$acc, $r" }

        Log.d("TEST+++", "RankingActivity $str")
    }

    override fun onResume() {
        super.onResume()
        ApplicationController.activityStarted()
    }
}