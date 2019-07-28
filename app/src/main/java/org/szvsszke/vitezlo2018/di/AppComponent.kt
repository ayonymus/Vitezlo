package org.szvsszke.vitezlo2018.di

import dagger.Component
import org.szvsszke.vitezlo2018.MapFragment
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    FrameworkModule:: class,
    GoogleMapsModule::class,
    ViewModelModule::class
])
@Singleton
interface AppComponent {

    fun inject(fragment: MapFragment)

}
