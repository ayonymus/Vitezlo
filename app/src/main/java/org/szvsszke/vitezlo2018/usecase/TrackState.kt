package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.entity.Track

sealed class TrackState {

    data class Data(val data: Track): TrackState()
    object Error: TrackState()

}
