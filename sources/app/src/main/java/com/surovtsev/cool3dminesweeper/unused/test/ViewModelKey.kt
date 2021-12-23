package com.surovtsev.cool3dminesweeper.unused.test

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(
    AnnotationRetention.RUNTIME
)
@MapKey
annotation class ViewModelKey(
    val value: KClass<out ViewModel>
)

/*
    fun viewModelFactory(): ViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(MainScreenViewModel::class)
    abstract fun mainScreenViewModel(viewModel: MainScreenViewModel): ViewModel

//                    val viewModel by viewModels<MainScreenViewModel> {
//                        appComponent.viewModelFactory<MainScreenViewModel>(
//                            savedStateRegistryOwner
//                        )
//                    }
 */
