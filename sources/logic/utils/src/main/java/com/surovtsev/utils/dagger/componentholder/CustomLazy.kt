package com.surovtsev.utils.dagger.componentholder

typealias ActionIfNotNull<C> = (component: C) -> Unit
typealias ValueBuilder<C> = () -> C

class CustomLazy<C: Any>(
    private val valueBuilder: ValueBuilder<C>,
) {
    var value: C? = null
        private set

    fun getOrCreate(
    ): C {
        return getOrCreate(null)
    }

    fun getOrCreate(
        actionIfNotNull: ActionIfNotNull<C>?
    ): C {
        val currValue = value

        return if (currValue != null) {
            actionIfNotNull?.invoke(currValue)
            currValue
        } else {
            create()
        }
    }

    private fun create(): C {
        val newSettingsComponent = valueBuilder()

        value = newSettingsComponent

        return newSettingsComponent
    }
}