package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save

import android.content.Context

class SaveController(
    context: Context
) {
    companion object {
        const val SaveJson = "SAVE_JSON"
    }

    val pref by lazy {
        context.getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    fun save(json: String) {
        with (pref.edit()) {
            putString(SaveJson, json)
        }.apply()
    }

    fun hasSave(): Boolean {
        return pref.contains(SaveJson)
    }

    fun loadData(): String {
        return pref.getString(SaveJson, "")!!
    }

    fun emptyData() {
        with(pref.edit()) {
            remove(SaveJson)
        }.apply()
    }
}