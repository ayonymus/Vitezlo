package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.Preferences
import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.domain.entity.Track
import javax.inject.Inject

class GetTouristPaths @Inject constructor(private val repository: Repository<List<Track>>,
                                          private val preferences: Preferences) {

    operator fun invoke(): TouristPathState {
        return if(preferences.areTouristPathsEnabled()) {
            getFromRepo()
        } else {
            TouristPathState.Disabled
        }
    }

    private fun getFromRepo() = when (val response = repository.getData()) {
        is Loading.Success -> TouristPathState.Data(response.data)
        else -> TouristPathState.Error
    }

}
