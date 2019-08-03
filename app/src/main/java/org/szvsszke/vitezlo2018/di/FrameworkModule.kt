package org.szvsszke.vitezlo2018.di

import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.data.repository.ParameteredDataSource
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.domain.entity.Description
import org.szvsszke.vitezlo2018.domain.entity.Sight
import org.szvsszke.vitezlo2018.domain.entity.Track
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.CheckpointLoader
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.GpxCheckpointMapper
import org.szvsszke.vitezlo2018.framework.localdata.description.DescriptionsLoader
import org.szvsszke.vitezlo2018.framework.localdata.description.DescriptionsXmlParser
import org.szvsszke.vitezlo2018.framework.localdata.sight.GpxSightMapper
import org.szvsszke.vitezlo2018.framework.localdata.sight.SightLoader
import org.szvsszke.vitezlo2018.framework.localdata.touristpath.TouristPathLoader
import org.szvsszke.vitezlo2018.framework.localdata.track.GpxTrackMapper
import org.szvsszke.vitezlo2018.framework.localdata.track.TrackLoader

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
    ): DataSource<List<Description>> =
            DescriptionsLoader(assets, xmlParser)

    @Provides
    fun provideTrackDataSource(assets: AssetManager, gpxParser: GPXParser, gpxTrackMapper: GpxTrackMapper
    ): ParameteredDataSource<String, Track> =
            TrackLoader(assets, gpxParser, gpxTrackMapper)

    @Provides
    fun provideTouristPathSource(assets: AssetManager, gpxParser: GPXParser, gpxTrackMapper: GpxTrackMapper
    ): DataSource<List<Track>> =
            TouristPathLoader(assets, gpxParser, gpxTrackMapper)

}
