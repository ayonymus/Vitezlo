package org.szvsszke.vitezlo2018.di

import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.domain.entity.Description
import org.szvsszke.vitezlo2018.domain.entity.Sight
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.CheckpointLoader
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.GpxCheckpointMapper
import org.szvsszke.vitezlo2018.framework.localdata.description.DescriptionsLoader
import org.szvsszke.vitezlo2018.framework.localdata.description.DescriptionsXmlParser
import org.szvsszke.vitezlo2018.framework.localdata.sight.GpxSightMapper
import org.szvsszke.vitezlo2018.framework.localdata.sight.SightLoader

@Module
class FrameworkModule {

    @Provides
    fun provideGpxParser() = GPXParser()

    @Provides
    fun provideCheckpointSource(assets: AssetManager, gpxParser: GPXParser, gpxCheckpointMapper: GpxCheckpointMapper
    ): DataSource<Map<String, Checkpoint>> =
            CheckpointLoader(assets, gpxParser, gpxCheckpointMapper)

    @Provides
    fun provideSightSource(assets: AssetManager, gpxParser: GPXParser, gpxSightMapper: GpxSightMapper
    ): DataSource<List<Sight>> =
            SightLoader(assets, gpxParser, gpxSightMapper)

    @Provides
    fun provideDescriptionXmlParser() = DescriptionsXmlParser()

    @Provides
    fun provideDescriptionSource(assets: AssetManager, xmlParser: DescriptionsXmlParser
    ): DataSource<List<Description>> = DescriptionsLoader(assets, xmlParser)

}
