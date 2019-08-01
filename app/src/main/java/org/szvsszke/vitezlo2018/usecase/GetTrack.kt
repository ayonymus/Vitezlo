package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.data.repository.track.TrackRepository
import org.szvsszke.vitezlo2018.domain.Loading
import javax.inject.Inject

class GetTrack @Inject constructor(private val trackRepository: TrackRepository) {

    fun invoke(trackName: String): TrackState {
        return when(val result = trackRepository.getData(trackName)) {
            is Loading.Success -> TrackState.Data(result.data)
            else -> TrackState.Error
        }
    }
}
