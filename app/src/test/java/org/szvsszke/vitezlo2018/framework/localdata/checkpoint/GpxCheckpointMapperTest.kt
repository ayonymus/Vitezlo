package org.szvsszke.vitezlo2018.framework.localdata.checkpoint

import io.ticofab.androidgpxparser.parser.domain.Gpx
import io.ticofab.androidgpxparser.parser.domain.WayPoint
import org.junit.Before
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.Checkpoint
import kotlin.test.assertEquals

class GpxCheckpointMapperTest {

    private lateinit var mapper: GpxCheckpointMapper

    @Before
    fun setUp() {
        mapper = GpxCheckpointMapper()
    }

    @Test
    fun `given a gpx with no waypoints when called then return an empty map`() {
        val gpx = Gpx.Builder()
                .setWayPoints(emptyList())
                .setTracks(emptyList())
                .setRoutes(emptyList())
                .build()

        val result = mapper.mapToCheckPointMap(gpx)

        assertEquals(emptyMap(), result)

    }

    @Test
    fun `given a gpx with waypoints when called then return a map of checkpoints`() {
        val name1 = "name1"
        val desc1 = "id1"
        val wayPointBuilder = WayPoint.Builder()
                .setLatitude(1.0)
                .setLongitude(1.0)
                .setName(name1)
                .setDesc(desc1)
        val waypoint1 = wayPointBuilder.build() as WayPoint

        val name2 = "name2"
        val desc2 = "id2"
        val wayPoint2 = wayPointBuilder.setName(name2)
                .setDesc(desc2)
                .build() as WayPoint

        val gpx = Gpx.Builder()
                .setWayPoints(listOf(waypoint1, wayPoint2))
                .setTracks(emptyList())
                .setRoutes(emptyList())
                .build()

        val expected = mapOf(
                Pair(desc1, Checkpoint(desc1, name1,0, 1.0, 1.0)),
                Pair(desc2, Checkpoint(desc2, name2,1, 1.0, 1.0)))

        val result = mapper.mapToCheckPointMap(gpx)

        assertEquals(expected, result)

    }

}
