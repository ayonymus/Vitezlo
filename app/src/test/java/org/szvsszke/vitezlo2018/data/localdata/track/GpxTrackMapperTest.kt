package org.szvsszke.vitezlo2018.data.localdata.track

import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.junit.Test
import kotlin.test.assertFails

//This will be moved to json, this seems like framework testing
internal class GpxTrackMapperTest {

    private val mapper = GpxTrackMapper()

    @Test
    fun `given a gpx with no tracks then throw exception`() {
        val gpx = Gpx.Builder()
                .setWayPoints(emptyList())
                .setTracks(emptyList())
                .setRoutes(emptyList())
                .build()
        assertFails { mapper.mapToTrack(gpx) }
    }

//    @Test
//    fun `given a gpx with tracks then create track from first`() {
//        val gpx = Gpx.Builder()
//                .setWayPoints(emptyList())
//                .setTracks(emptyList())
//                .setRoutes(emptyList())
//                .build()
//
//        val result = mapper.mapToTrack(gpx)
//
//    }
}
