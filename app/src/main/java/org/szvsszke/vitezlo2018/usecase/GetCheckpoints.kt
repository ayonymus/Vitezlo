package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.data.CheckpointListMapper
import org.szvsszke.vitezlo2018.data.repository.checkpoint.CheckpointRepository
import org.szvsszke.vitezlo2018.domain.Result
import javax.inject.Inject

class GetCheckpoints @Inject constructor(private val repository: CheckpointRepository,
                                         private val mapper: CheckpointListMapper) {

    operator fun invoke(checkPointIds: Array<String>): CheckpointState =
            when (val data = repository.getData()) {
                is Result.Data -> CheckpointState.Data(mapper.mapCheckpoint(data.data, checkPointIds))
                else -> CheckpointState.Error
            }
}
