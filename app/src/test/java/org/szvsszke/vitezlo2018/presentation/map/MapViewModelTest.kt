package org.szvsszke.vitezlo2018.presentation.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.*
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus
import org.szvsszke.vitezlo2018.usecase.*
import kotlin.test.assertEquals

internal class MapViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val id = "checkpoint"
    private val checkpoints = listOf(mock<Checkpoint> { })
    private val getCheckpoints = mock<GetCheckpoints> {
        on { invoke(listOf(id)) } doReturn CheckpointState.Data(checkpoints)
    }

    private val sight = mock<Sight> { }
    private val sights = listOf(sight)
    private val getSights = mock<GetSights> {
        on { invoke() } doReturn SightsState.Data(sights)
    }

    private val description = mock<Description> { }
    private val descriptions = listOf(description)
    private val getDescriptions = mock<GetDescriptions> {
        on { invoke() } doReturn DescriptionsState.Data(descriptions)
    }

    private val track = mock<Track> { }
    private val trackId = "some track"
    private val getTrack = mock<GetTrack> {
        on { invoke(trackId) } doReturn TrackState.Data(track)
    }

    private val getTouristPaths = mock<GetTouristPaths> {
        on { invoke() } doReturn TouristPathState.Data(listOf(track))
    }

    private val mapStatus = MapStatus(3, Point(12.0, 12.0), 1F)
    private val getMapStatus = mock<GetMapStatus> {
        on { invoke() } doReturn mapStatus
    }
    private val saveMapStatus = mock<SaveMapStatus> { }

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        viewModel = MapViewModel(getCheckpoints, getSights, getDescriptions, getTrack, getTouristPaths,
                getMapStatus, saveMapStatus, Dispatchers.Unconfined)
    }

    @Test
    fun `given checkpoint data when observed then return checkpoint state`() {
        viewModel.getCheckpoints(listOf(id))
        assertEquals(CheckpointState.Data(checkpoints), viewModel.getCheckpoints(listOf(id)).value)
    }

    @Test
    fun `given sight data when observed then return sights state`() {
        viewModel.getSights()
        assertEquals(SightsState.Data(sights), viewModel.getSights().value)
    }

    @Test
    fun `given description data when observed then return description state`() {
        viewModel.getDescriptions()
        assertEquals(DescriptionsState.Data(descriptions), viewModel.getDescriptions().value)
    }

    @Test
    fun `given track data when observed then return track state`() {
        viewModel.getTrack(trackId)
        assertEquals(TrackState.Data(track), viewModel.getTrack(trackId).value)
    }

    @Test
    fun `given tourist path data when observed then return tourist path state`() {
        viewModel.getTouristPath()
        assertEquals(TouristPathState.Data(listOf(track)), viewModel.getTouristPath().value)
    }

    @Test
    fun `given map when observed then return map settings`() {
        val mapStatus = viewModel.getMapStatus()
        assertEquals(mapStatus, mapStatus)
    }

    @Test
    fun `given map when destroyed then save map status`() {
        viewModel.saveMapStatus(mapStatus)
        verify(saveMapStatus).invoke(mapStatus)
    }
}
