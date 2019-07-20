package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Checkpoint
import org.szvsszke.vitezlo2018.framework.localdata.CheckpointLoader

/**
 * Repository for handling the checkpoint data
 */
class CheckpointRepository(private val checkpointLoader: CheckpointLoader) {

    private var cache: List<Checkpoint>? = null

    fun getData(): List<Checkpoint>? {
        if(cache == null) {
            cache = checkpointLoader.loadData()
        }
        return if(cache != null) {
            cache
        } else {
            emptyList()
        }
    }

}
