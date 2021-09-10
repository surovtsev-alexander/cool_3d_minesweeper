package com.surovtsev.cool_3d_minesweeper.utils.live_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsData

class MyLiveData<T>(
    val defaultValue: T
) {
    private val _data = MutableLiveData<T>(defaultValue)
    val data: LiveData<T> = _data

    fun onDataChanged(newValue: T) {
        _data.value = newValue
    }
}
