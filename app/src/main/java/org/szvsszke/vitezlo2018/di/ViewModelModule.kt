package org.szvsszke.vitezlo2018.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import org.szvsszke.vitezlo2018.presentation.ViewModelFactory
import org.szvsszke.vitezlo2018.presentation.ViewModelKey
import org.szvsszke.vitezlo2018.presentation.map.MapViewModel

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun provideMapViewModel(viewModel: MapViewModel): ViewModel

}
