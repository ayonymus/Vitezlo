package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.entity.Track

sealed class TouristPathState {

    data class Data(val data: List<Track>): TouristPathState()
    object Error: TouristPathState()
    object Disabled : TouristPathState()

}
