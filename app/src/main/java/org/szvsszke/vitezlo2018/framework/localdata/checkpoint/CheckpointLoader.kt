package org.szvsszke.vitezlo2018.framework.localdata.checkpoint

import org.szvsszke.vitezlo2018.domain.Checkpoint
import org.szvsszke.vitezlo2018.framework.localdata.LocalDataStorage

class CheckpointLoader: LocalDataStorage<Map<String, Checkpoint>> {

    override fun load(): Map<String, Checkpoint>? {
        return emptyMap()
    }

}
