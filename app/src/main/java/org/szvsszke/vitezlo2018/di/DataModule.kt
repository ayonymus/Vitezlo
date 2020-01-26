package org.szvsszke.vitezlo2018.di

import android.content.SharedPreferences
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.localdata.checkpoint.CheckpointLoader
import org.szvsszke.vitezlo2018.data.localdata.checkpoint.GpxCheckpointMapper
import org.szvsszke.vitezlo2018.data.localdata.description.DescriptionsLoader
import org.szvsszke.vitezlo2018.data.localdata.description.DescriptionsXmlParser
import org.szvsszke.vitezlo2018.data.localdata.sight.GpxSightMapper
import org.szvsszke.vitezlo2018.data.localdata.sight.SightLoader
import org.szvsszke.vitezlo2018.data.localdata.touristpath.TouristPathLoader
import org.szvsszke.vitezlo2018.data.localdata.track.GpxTrackMapper
import org.szvsszke.vitezlo2018.data.localdata.track.TrackLoader
import org.szvsszke.vitezlo2018.data.preferences.SharedInfoBoxPreferences
import org.szvsszke.vitezlo2018.data.preferences.SharedMapPreferences
import org.szvsszke.vitezlo2018.data.preferences.SharedUserPreferences
import org.szvsszke.vitezlo2018.data.repository.BaseRepository
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.data.repository.ParameteredDataSource
import org.szvsszke.vitezlo2018.data.repository.checkpoint.CheckpointRepository
import org.szvsszke.vitezlo2018.data.repository.descriptions.DescriptionRepository
import org.szvsszke.vitezlo2018.data.repository.sight.SightRepository
import org.szvsszke.vitezlo2018.data.repository.track.TrackRepository
import org.szvsszke.vitezlo2018.domain.MappingRepository
import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.domain.entity.Description
import org.szvsszke.vitezlo2018.domain.entity.Sight
import org.szvsszke.vitezlo2018.domain.entity.Track
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxPreferences
import org.szvsszke.vitezlo2018.domain.preferences.MapPreferences
import org.szvsszke.vitezlo2018.domain.preferences.UserPreferences

@Module
class DataModule {

    @Provides
    fun provideDescriptionsRepository(repository: DescriptionRepository): Repository<List<Description>> = repository

    @Provides
    fun provideTrackRepository(repository: TrackRepository): MappingRepository<String, Track> = repository

    @Provides
    fun provideCheckpointRepository(repository: CheckpointRepository): Repository<Map<String, Checkpoint>>  = repository

    @Provides
    fun provideSightsRepository(repository: SightRepository): Repository<List<Sight>> = repository

    @Provides
    fun provideTouristPathRepository(source: DataSource<List<Track>>): Repository<List<Track>> = BaseRepository(source)

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

    @Provides
    fun provideUserPreferences(sharedPreferences: SharedPreferences): UserPreferences =
            SharedUserPreferences(sharedPreferences)

    @Provides
    fun provideMapPreferences(sharedPreferences: SharedPreferences): MapPreferences =
            SharedMapPreferences(sharedPreferences)

    @Provides
    fun provideInfoBoxPreferences(sharedPreferences: SharedPreferences): InfoBoxPreferences =
            SharedInfoBoxPreferences(sharedPreferences)

}
