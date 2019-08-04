package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.entity.Checkpoint

sealed class CheckpointState {

    data class Data(val data: List<Checkpoint>): CheckpointState()
    object Error: CheckpointState()
    object Disabled : CheckpointState()

}
