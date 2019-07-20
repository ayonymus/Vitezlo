package org.szvsszke.vitezlo2018.framework.localdata.checkpoint

import org.szvsszke.vitezlo2018.domain.Checkpoint
import org.szvsszke.vitezlo2018.framework.localdata.LocalDataStorage

class CheckpointLoader: LocalDataStorage<List<Checkpoint>> {

    override fun load(): List<Checkpoint>? {
        return emptyList()
    }

}
