package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.domain.entity.Track
import javax.inject.Inject

class GetTouristPaths @Inject constructor(private val repository: Repository<List<Track>>) {

    operator fun invoke() = when (val response = repository.getData()) {
        is Loading.Success -> TouristPathState.Data(response.data)
        else -> TouristPathState.Error
    }

}
