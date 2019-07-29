package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.data.repository.sight.SightRepository
import org.szvsszke.vitezlo2018.domain.Loading
import javax.inject.Inject

class GetSights @Inject constructor(private val sightRepository: SightRepository) {

    fun invoke(): SightsState {
        return when(val result = sightRepository.getData()) {
            is Loading.Success -> SightsState.Data(result.data)
            else -> SightsState.Error
        }
    }

}
