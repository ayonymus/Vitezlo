package org.szvsszke.vitezlo2018.data.localdata.track

import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.domain.entity.Track
import javax.inject.Inject

class GpxTrackMapper @Inject constructor() {

    fun mapToTrack(gpx: Gpx): Track {
        val gpxTrack = gpx.tracks.first()
        return Track(gpxTrack.trackName, gpxTrack.trackSegments.first()
                .trackPoints.map { Point(it.latitude, it.longitude) }, gpxTrack.trackDesc)
    }

}
