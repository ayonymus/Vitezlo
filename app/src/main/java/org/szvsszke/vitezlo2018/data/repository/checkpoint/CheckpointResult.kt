package org.szvsszke.vitezlo2018.data.repository.checkpoint

import org.szvsszke.vitezlo2018.domain.entity.Checkpoint

sealed class CheckpointResult {

    object Empty: CheckpointResult()
    data class Data(val data: Map<String, Checkpoint>): CheckpointResult()
    object Error: CheckpointResult()

}
