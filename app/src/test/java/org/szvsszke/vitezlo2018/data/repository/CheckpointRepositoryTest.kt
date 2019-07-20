package org.szvsszke.vitezlo2018.data.repository

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.szvsszke.vitezlo2018.framework.localdata.CheckpointLoader
import org.szvsszke.vitezlo2018.map.model.Waypoint
import kotlin.test.assertEquals

class CheckpointRepositoryTest {

    private val waypoint1 = mock<Waypoint> { }
    private val waypoint2 = mock<Waypoint> { }
    private val waypoints = listOf(waypoint1, waypoint2)

    private val checkpointLoader = mock<CheckpointLoader> {
        on { loadData() } doReturn waypoints
    }

    private lateinit var repository: CheckpointRepository

    @Before
    fun setUp() {
        repository = CheckpointRepository(checkpointLoader)
    }

    @Test
    fun `given no cached data when data requested then should load from disk`() {
        val result = repository.getData()

        assertEquals(waypoints, result)

        verify(checkpointLoader, times(1)).loadData()
    }

}