package com.surovtsev.utils.dataconstructions

@Suppress("unused")
class MyLazyVal<T>(
    private val initAction: () -> T
) {
    private var _value: T? = null
    val value: T
        get(): T {
            if (_value == null) {
                _value = initAction()
            }
            return _value!!
        }
}