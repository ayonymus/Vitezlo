package org.szvsszke.vitezlo2018.framework.localdata.checkpoint

import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.szvsszke.vitezlo2018.domain.Checkpoint

/**
 * Maps the Gpx data into a map of Checkpoints
 */
class GpxCheckpointMapper {

    fun mapToCheckPointMap(gpx: Gpx): Map<String, Checkpoint> {
        val map = HashMap<String, Checkpoint>()

        gpx.wayPoints.forEachIndexed { index, wayPoint ->
            wayPoint.apply {
                map[desc] = Checkpoint(desc, name, index, latitude, longitude)
            }
        }
        return map
    }

}
