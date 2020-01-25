package org.szvsszke.vitezlo2018.data.localdata.sight

import io.ticofab.androidgpxparser.parser.domain.Gpx
import io.ticofab.androidgpxparser.parser.domain.WayPoint
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Sight
import kotlin.test.assertEquals

internal class GpxSightMapperTest {

    private val mapper = GpxSightMapper()

    @Test
    fun `given a gpx with no waypoints when called then return an empty list`() {
        val gpx = Gpx.Builder()
                .setWayPoints(emptyList())
                .setTracks(emptyList())
                .setRoutes(emptyList())
                .build()

        val result = mapper.mapToSightList(gpx)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `given a gpx with waypoints when called then return a list of sights`() {
        val name1 = "name1"
        val desc1 = "some url"
        val wayPointBuilder = WayPoint.Builder()
                .setLatitude(1.0)
                .setLongitude(2.0)
                .setName(name1)
                .setDesc(desc1)
        val waypoint1 = wayPointBuilder.build() as WayPoint

        val name2 = "name2"
        val wayPoint2 = WayPoint.Builder().setName(name2)
                .setLatitude(3.0)
                .setLongitude(4.0)
                .build() as WayPoint

        val gpx = Gpx.Builder()
                .setWayPoints(listOf(waypoint1, wayPoint2))
                .setTracks(emptyList())
                .setRoutes(emptyList())
                .build()

        val expected = listOf(
                Sight(name1, 1.0, 2.0, desc1),
                Sight(name2, 3.0, 4.0, null))

        val result = mapper.mapToSightList(gpx)

        assertEquals(expected, result)

    }
}
