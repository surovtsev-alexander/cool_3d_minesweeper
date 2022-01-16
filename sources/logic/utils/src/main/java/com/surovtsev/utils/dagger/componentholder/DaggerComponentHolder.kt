package com.surovtsev.utils.dagger.componentholder

typealias ActionIfNotNull<C> = (component: C) -> Unit
typealias ComponentBuilder<C> = () -> C

class DaggerComponentHolder<C: Any>(
    private val componentBuilder: ComponentBuilder<C>,
) {
    var component: C? = null
        private set

    fun getOrCreate(
    ): C {
        return getOrCreate(null)
    }

    fun getOrCreate(
        actionIfNotNull: ActionIfNotNull<C>?
    ): C {
        val currValue = component

        return if (currValue != null) {
            actionIfNotNull?.invoke(currValue)
            currValue
        } else {
            create()
        }
    }

    private fun create(): C {
        val newSettingsComponent = componentBuilder()

        component = newSettingsComponent

        return newSettingsComponent
    }
}