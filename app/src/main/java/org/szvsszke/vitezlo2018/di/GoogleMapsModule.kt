package org.szvsszke.vitezlo2018.di

import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.maps.android.ui.IconGenerator
import dagger.Module
import dagger.Provides
import org.szvsszke.vitezlo2018.data.repository.BaseMappingRepository
import org.szvsszke.vitezlo2018.domain.MappingRepository
import org.szvsszke.vitezlo2018.presentation.map.marker.CheckpointIconSource

@Module
class GoogleMapsModule {

    @Provides
    fun provideIconGenerator(context: Context) = IconGenerator(context)

    @Provides
    fun provideIconRepository(iconSource: CheckpointIconSource): MappingRepository<String, BitmapDescriptor> =
            BaseMappingRepository(iconSource)

}
