package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class GetCheckpointListFromMap @Inject constructor() {

    operator fun invoke(checkpointMap: Map<String, Checkpoint>, ids: List<String>): List<Checkpoint> {
        val checkPoints = ArrayList<Checkpoint>()
        ids.forEach { id ->
            checkpointMap[id]?.apply {
                checkPoints.add(this)
            } ?: run { Timber.e("Id not found in checkpoints: %s", id) }
        }
        return checkPoints
    }
}
