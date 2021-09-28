package com.surovtsev.cool_3d_minesweeper.utils.data_constructions

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