package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import javax.inject.Inject

class GetCheckpoints @Inject constructor(private val repository: Repository<Map<String, Checkpoint>>,
                                         private val mapper: GetCheckpointListFromMap) {

    operator fun invoke(checkPointIds: Array<String>): CheckpointState =
            when (val data = repository.getData()) {
                is Loading.Success -> CheckpointState.Data(mapper.invoke(data.data, checkPointIds))
                else -> CheckpointState.Error
            }
}
