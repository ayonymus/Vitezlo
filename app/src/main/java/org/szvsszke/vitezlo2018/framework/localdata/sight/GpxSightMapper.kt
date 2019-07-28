package org.szvsszke.vitezlo2018.framework.localdata.sight

import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.szvsszke.vitezlo2018.domain.entity.Sight

class GpxSightMapper {

    fun mapToSightList(gpx: Gpx): List<Sight> = gpx.wayPoints.map { wayPoint ->
            Sight(wayPoint.name, wayPoint.latitude, wayPoint.longitude, wayPoint.desc)
    }

}
