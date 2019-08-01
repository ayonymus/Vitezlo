package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.data.repository.descriptions.DescriptionRepository
import org.szvsszke.vitezlo2018.domain.Loading
import javax.inject.Inject

class GetDescriptions @Inject constructor(private val descriptionRepository: DescriptionRepository) {

    fun invoke() = when(val result = descriptionRepository.getData()) {
        is Loading.Success -> DescriptionsState.Data(result.data)
        else -> DescriptionsState.Error
    }

}
