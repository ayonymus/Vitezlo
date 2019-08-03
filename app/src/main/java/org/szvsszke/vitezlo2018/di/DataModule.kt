package org.szvsszke.vitezlo2018.di

import dagger.Module
import dagger.Provides
import org.szvsszke.vitezlo2018.data.repository.BaseRepository
import org.szvsszke.vitezlo2018.data.repository.DataSource
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

}
