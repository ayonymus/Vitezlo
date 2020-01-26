package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.preferences.UserPreferences
import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import javax.inject.Inject

class GetCheckpoints @Inject constructor(private val repository: Repository<Map<String, Checkpoint>>,
                                         private val mapper: GetCheckpointListFromMap,
                                         private val userPreferences: UserPreferences) {

    operator fun invoke(checkPointIds: List<String>): CheckpointState {
        return if(!userPreferences.areCheckPointsEnabled()) {
            CheckpointState.Disabled
        } else {
            queryRepository(checkPointIds)
        }
    }

    private fun queryRepository(checkPointIds: List<String>) = when (val data = repository.getData()) {
        is Loading.Success -> CheckpointState.Data(mapper.invoke(data.data, checkPointIds))
        else -> CheckpointState.Error
    }


}
