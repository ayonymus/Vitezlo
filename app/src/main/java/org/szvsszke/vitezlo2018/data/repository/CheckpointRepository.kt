package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.framework.localdata.CheckpointLoader
import org.szvsszke.vitezlo2018.map.model.Waypoint

/**
 * Repository for handling the checkpoint data
 */
class CheckpointRepository(private val checkpointLoader: CheckpointLoader) {

    fun getData(): List<Waypoint> {
        return checkpointLoader.loadData()
    }

}