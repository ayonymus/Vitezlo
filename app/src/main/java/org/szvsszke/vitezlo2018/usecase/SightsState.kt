package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.entity.Sight

sealed class SightsState {

    data class Data(val data: List<Sight>): SightsState()
    object Error: SightsState()
    object Disabled : SightsState()

}
