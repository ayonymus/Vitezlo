package org.szvsszke.vitezlo2018.framework.localdata

import org.szvsszke.vitezlo2018.map.model.Waypoint

interface CheckpointLoader {

    fun loadData(): List<Waypoint>

}
