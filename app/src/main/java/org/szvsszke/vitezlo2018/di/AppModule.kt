package org.szvsszke.vitezlo2018.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val appContext: Context) {

    @Provides
    fun provideApplicationContext(): Context = appContext

}
