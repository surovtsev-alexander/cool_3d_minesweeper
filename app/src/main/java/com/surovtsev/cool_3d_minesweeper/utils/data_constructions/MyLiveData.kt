package com.surovtsev.cool_3d_minesweeper.utils.data_constructions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MyLiveData<T>(
    val defaultValue: T
) {
    private val _data = MutableLiveData<T>(defaultValue)
    val data: LiveData<T> = _data

    fun onDataChanged(newValue: T) {
        _data.value = newValue
    }

    fun getValueOrDefault() =
        data.value ?: defaultValue
}