package com.surovtsev.utils.dataconstructions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class MyLiveData<T: Any>(
    val defaultValue: T
) {
    private val _data = MutableLiveData(defaultValue)
    val data: LiveData<T> = _data

    open fun onDataChanged(newValue: T) {
        _data.value = newValue
    }

    val valueOrDefault: T
        get () = data.value ?: defaultValue
}