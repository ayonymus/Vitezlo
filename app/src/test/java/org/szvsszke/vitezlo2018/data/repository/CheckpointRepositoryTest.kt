package org.szvsszke.vitezlo2018.data.repository

import com.nhaarman.mockitokotlin2.*
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

    @Test
    fun `given cached data when data requested then should not load from disk again`() {
        repository.getData()
        val result = repository.getData()

        assertEquals(waypoints, result)
        verify(checkpointLoader, times(1)).loadData()
    }

    @Test
    fun `given data is empty then should return empty list`() {
        given { checkpointLoader.loadData() }.willReturn(emptyList())

        val result = repository.getData()

        assertEquals(emptyList(), result)
        verify(checkpointLoader, times(1)).loadData()
    }
}
