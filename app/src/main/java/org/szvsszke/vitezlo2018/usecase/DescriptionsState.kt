package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.entity.Description

sealed class DescriptionsState {

    data class Data(val data: List<Description>): DescriptionsState()
    object Error: DescriptionsState()
}
