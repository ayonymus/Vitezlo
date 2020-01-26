package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.preferences.UserPreferences
import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.domain.entity.Sight
import javax.inject.Inject

class GetSights @Inject constructor(private val sightRepository: Repository<List<Sight>>,
                                    private var userPreferences: UserPreferences) {

    fun invoke() = if (userPreferences.areSightsEnabled()) {
        getFromRepo()
    } else {
        SightsState.Disabled
    }

    private fun getFromRepo(): SightsState {
        return when(val result = sightRepository.getData()) {
            is Loading.Success -> SightsState.Data(result.data)
            else -> SightsState.Error
        }
    }

}
