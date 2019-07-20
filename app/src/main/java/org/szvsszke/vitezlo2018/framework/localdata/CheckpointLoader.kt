package org.szvsszke.vitezlo2018.framework.localdata

import org.szvsszke.vitezlo2018.domain.Checkpoint

interface CheckpointLoader {

    fun loadData(): List<Checkpoint>?

}
