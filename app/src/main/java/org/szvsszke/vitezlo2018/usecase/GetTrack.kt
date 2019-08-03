package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.MappingRepository
import org.szvsszke.vitezlo2018.domain.entity.Track
import javax.inject.Inject

class GetTrack @Inject constructor(private val trackRepository: MappingRepository<String, Track>) {

    fun invoke(trackName: String): TrackState {
        return when(val result = trackRepository.getData(trackName)) {
            is Loading.Success -> TrackState.Data(result.data)
            else -> TrackState.Error
        }
    }
}
