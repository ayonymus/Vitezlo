package org.szvsszke.vitezlo2018.di

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
class AppModule(private val appContext: Context) {

    @Provides
    fun provideApplicationContext(): Context = appContext

    @Provides
    fun provideAssetManager(context: Context) = context.assets!!

    @Provides
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

}
