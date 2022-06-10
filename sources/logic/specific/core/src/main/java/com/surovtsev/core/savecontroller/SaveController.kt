/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.core.savecontroller

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.surovtsev.core.room.entities.Settings

class SaveController(
    context: Context
) {
    val gson by lazy {
        Gson()
    }

    private val pref by lazy {
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
            val res = gson.fromJson(
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
        tryToLoad(
            SaveTypes.GameSettingsJson
        )?: Settings.SettingsData()

    fun <T: Any> save(name: String, data: T) {
        val text = gson.toJson(data)
        save(name, text)
    }
}