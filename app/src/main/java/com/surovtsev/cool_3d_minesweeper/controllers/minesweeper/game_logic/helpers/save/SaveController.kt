package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.surovtsev.cool_3d_minesweeper.dagger.app.AppScope
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import java.lang.Exception
import javax.inject.Inject

@AppScope
class SaveController @Inject constructor(
    context: Context
) {
    val gson by lazy {
        Gson()
    }

    val pref by lazy {
        context.getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    fun save(name: String, json: String) {
        with (pref.edit()) {
            putString(name, json)
        }.apply()
    }

    fun hasData(name: String): Boolean {
        return pref.contains(name)
    }

    fun loadData(name: String): String {
        return pref.getString(name, "")!!
    }

    fun emptyData(name: String) {
        with(pref.edit()) {
            remove(name)
        }.apply()
    }

    inline fun <reified T: Any> tryToLoad(name: String): T? {
        if (!hasData(name)){
            return null
        }
        return try {
            val data = loadData(name)
            val res = gson.fromJson<T>(
                data,
                T::class.java
            )
            res
        } catch (ex: Exception) {
            Log.d(
                "Minesweeper",
                "error while loading save\n${ex.message}\n${ex.printStackTrace()}")
            emptyData(name)
            null
        }
    }

    fun loadSettingDataOrDefault() =
        tryToLoad<SettingsData>(
            SaveTypes.GameSettingsJson
        )?: SettingsData()

    fun <T: Any> save(name: String, data: T) {
        val text = gson.toJson(data)
        save(name, text)
    }
}