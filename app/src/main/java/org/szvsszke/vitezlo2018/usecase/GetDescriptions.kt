package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.domain.entity.Description
import javax.inject.Inject

class GetDescriptions @Inject constructor(private val descriptionRepository: Repository<List<Description>>) {

    fun invoke() = when(val result = descriptionRepository.getData()) {
        is Loading.Success -> DescriptionsState.Data(result.data)
        else -> DescriptionsState.Error
    }

}
