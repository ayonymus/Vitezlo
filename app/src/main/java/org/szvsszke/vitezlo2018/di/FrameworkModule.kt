package org.szvsszke.vitezlo2018.di

import dagger.Module
import dagger.Provides
import io.ticofab.androidgpxparser.parser.GPXParser

@Module
class FrameworkModule {

    @Provides
    fun provideGpxParser() = GPXParser()

}
