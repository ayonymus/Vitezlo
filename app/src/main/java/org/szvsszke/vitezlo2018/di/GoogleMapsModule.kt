package org.szvsszke.vitezlo2018.di

import android.content.Context
import com.google.maps.android.ui.IconGenerator
import dagger.Module
import dagger.Provides

@Module
class GoogleMapsModule {

    @Provides
    fun provideIconGenerator(context: Context) = IconGenerator(context)

}
